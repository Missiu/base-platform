package com.example.template.manager.oss.ali;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.template.common.base.CommonConstants;
import com.example.template.common.base.ErrorCode;
import com.example.template.exception.customize.RemoteServiceException;
import com.example.template.exception.customize.ServiceException;
import com.example.template.manager.oss.ali.condition.OssAliCondition;
import com.example.template.manager.oss.ali.properties.AliProperties;
import com.example.template.module.domain.entity.File;
import com.example.template.module.service.FileService;
import com.example.template.util.EncryptUtils;
import com.example.template.util.ThrowUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 阿里云 OSS 配置
 */
@Service
@EnableConfigurationProperties(AliProperties.class)
@RequiredArgsConstructor
@Slf4j
@Conditional(OssAliCondition.class)
public class AliConfiguration {

    private final AliProperties aliProperties;
    private final FileService fileService;

    /**
     * 上传文件到 OSS
     *
     * @param file     待上传的文件
     * @param rootPath 上传的路径
     */
    @Transactional(rollbackFor = Exception.class)
    public File uploadToOss(MultipartFile file, String rootPath) {
        ThrowUtils.clientExceptionThrowIf(Objects.isNull(file), ErrorCode.USER_ERROR_A0700, "文件为空");

        String originalName = StringUtils.defaultIfBlank(file.getOriginalFilename(), file.getName());
        String suffix = FileUtil.getSuffix(originalName);
        String loginId = StpUtil.getSession().getLoginId().toString();

        try (InputStream inputStream = file.getInputStream()) {
            log.info("用户 {} 开始上传文件 : {} 到 OSS", loginId, originalName);
            return handleOssUpload(inputStream, originalName, suffix, rootPath);
        } catch (IOException e) {
            log.error("用户 {} 上传文件到 OSS 失败 : {}", loginId, e.getMessage());
            throw new RemoteServiceException(ErrorCode.USER_ERROR_A0701, e.getMessage());
        }
    }

    /**
     * 通过字节数组上传文件到 OSS
     */
    @Transactional(rollbackFor = Exception.class)
    public File uploadToOss(byte[] bytes, String originalName, String suffix, String rootPath) {
        ThrowUtils.clientExceptionThrowIf(Objects.isNull(bytes), ErrorCode.USER_ERROR_A0700, "文件为空");
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            return handleOssUpload(inputStream, originalName, suffix, rootPath);
        } catch (IOException e) {
            log.error("字节数组上传失败 : {}", e.getMessage());
            throw new RemoteServiceException(ErrorCode.USER_ERROR_A0701, e.getMessage());
        }
    }

    /**
     * 删除 OSS 中的文件
     *
     * @param id 文件 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteInOss(Long id) {
        File fileInDatabase = fileService.getOne(new LambdaQueryWrapper<File>()
                .eq(File::getId, id)
                .eq(File::getFileStorageType, "ali"));
        ThrowUtils.clientExceptionThrowIf(Objects.isNull(fileInDatabase), ErrorCode.USER_ERROR_A0700, "文件不存在");

        boolean deleted = fileService.removeById(id);
        ThrowUtils.serverExceptionThrowIfNot(deleted, ErrorCode.USER_ERROR_A0700, "文件删除失败");

        deleteInOssDirect(fileInDatabase.getFileUrl());
    }

    /**
     * 从 OSS 中直接删除文件
     */
    private void deleteInOssDirect(String url) {
        ThrowUtils.clientExceptionThrowIf(StringUtils.isBlank(url), ErrorCode.USER_ERROR_A0700, "文件 URL 为空");

        OSS ossClient = OssClientUtil.getOssClient(aliProperties);
        String[] split = url.split(aliProperties.getBucketName() + "." + aliProperties.getEndpoint().split(CommonConstants.HTTPS)[1] + "/");
        ThrowUtils.clientExceptionThrowIf(split.length != 2, ErrorCode.USER_ERROR_A0700, "文件 URL 格式错误");

        String key = split[1];
        try {
            ossClient.deleteObject(aliProperties.getBucketName(), key);
        } catch (OSSException | ClientException e) {
            log.error("OSS 删除文件失败 : {}", e.getMessage());
            throw new ServiceException(ErrorCode.USER_ERROR_A0700, e.getMessage());
        } finally {
            OssClientUtil.shutdownOssClient(ossClient);
        }
    }

    /**
     * 通用上传处理逻辑，处理文件的 OSS 上传
     */
    public File handleOssUpload(InputStream inputStream, String originalName, String suffix, String rootPath) throws IOException {
        OSS ossClient = OssClientUtil.getOssClient(aliProperties);
        try (inputStream) {
            byte[] dataBytes = inputStream.readAllBytes();

            String uniqueKey = EncryptUtils.generateHash(new String(dataBytes) + originalName + suffix);
            ThrowUtils.serverExceptionThrowIf(Objects.isNull(uniqueKey), ErrorCode.SYSTEM_ERROR_B0001);

            // 秒传判断
            File fastUploadResult = existAndFastUpload(uniqueKey);
            if (Objects.nonNull(fastUploadResult)) {
                return fastUploadResult;
            }

            // 普通上传
            String fileName = generateFileName(suffix);
            String key = buildOssKey(rootPath, fileName);
            try (InputStream uploadStream = new ByteArrayInputStream(dataBytes)) {
                ossClient.putObject(new PutObjectRequest(aliProperties.getBucketName(), key, uploadStream)
                        .withProgressListener(new PutObjectProgressListener()));
            }

            String url = generateFileUrl(key);
            return saveFileMetadata(originalName, suffix, dataBytes.length, fileName, url);
        } catch (OSSException | ClientException e) {
            log.error("OSS 上传文件失败: {}", e.getMessage());
            throw new ServiceException(ErrorCode.USER_ERROR_A0701, e.getMessage());
        } finally {
            OssClientUtil.shutdownOssClient(ossClient);
        }
    }

    /**
     * 生成 OSS 文件名
     */
    private String generateFileName(String suffix) {
        suffix = StringUtils.defaultIfBlank(suffix, CommonConstants.UNKNOWN_FILE_TYPE_SUFFIX).toLowerCase();
        return RandomUtil.randomString(16) + System.currentTimeMillis() + "." + suffix;
    }

    /**
     * 构建 OSS 存储路径
     */
    private String buildOssKey(String rootPath, String fileName) {
        return StringUtils.isBlank(rootPath) ? fileName : rootPath + "/" + fileName;
    }

    /**
     * 生成文件 URL
     */
    private String generateFileUrl(String key) {
        return CommonConstants.HTTPS + aliProperties.getBucketName() + "." + aliProperties.getEndpoint().split(CommonConstants.HTTPS)[1] + "/" + key;
    }

    /**
     * 保存文件元数据
     */
    private File saveFileMetadata(String originalName, String suffix, int fileSize, String fileName, String url) {
        File newFile = new File()
                .setFileName(fileName)
                .setFileOriginalName(originalName)
                .setFileSuffix(suffix)
                .setFileSize((long) fileSize)
                .setFileUrl(url)
                .setFileStorageType("ali");
        boolean saved = fileService.save(newFile);
        ThrowUtils.serverExceptionThrowIfNot(saved, ErrorCode.USER_ERROR_A0700, "文件上传失败");
        return newFile;
    }

    /**
     * 秒传文件判断
     */
    private File existAndFastUpload(String uniqueKey) {
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(File::getUniqueKey, uniqueKey).eq(File::getFileStorageType, "ali");
        return fileService.getOne(queryWrapper);
    }

}
