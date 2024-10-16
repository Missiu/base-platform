package com.example.template.manager.oss.tencent;

import com.example.template.common.base.ErrorCode;
import com.example.template.exception.customize.RemoteServiceException;
import com.example.template.exception.customize.ServiceException;
import com.example.template.manager.oss.tencent.properties.TencentProperties;
import com.example.template.util.ThrowUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 腾讯云COS文件服务类
 * 处理文件上传、删除和查询等操作
 *
 * @Author: AntonyCheng
 */
@Service
@AllArgsConstructor
@Slf4j
public class TencentFileService {

    private final TencentConfiguration tencentConfiguration;
    private final TencentProperties tencentProperties;

    /**
     * 上传文件到COS
     *
     * @param file     待上传的文件
     * @param rootPath 上传的路径
     * @return 上传的文件信息
     */
    public String uploadToCos(MultipartFile file, String rootPath) {
        //  校验上传的文件
        ThrowUtils.clientExceptionThrowIf(file == null || file.isEmpty(), ErrorCode.USER_ERROR_A0700);
        String originalName = file.getOriginalFilename();
        String suffix = getFileSuffix(originalName);

        try (InputStream inputStream = file.getInputStream()) {
            return uploadToCos(inputStream, originalName, suffix, rootPath);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            throw new RemoteServiceException(ErrorCode.SERVICE_ERROR_C0001, e.getMessage());
        }
    }

    /**
     * 上传文件流到COS
     *
     * @param inputStream  待上传的文件流
     * @param originalName 文件原名称
     * @param suffix       文件后缀
     * @param rootPath     上传的路径
     * @return 上传的文件的COS路径
     */
    public String uploadToCos(InputStream inputStream, String originalName, String suffix, String rootPath) {
        // 校验输入流是否为空
        ThrowUtils.clientExceptionThrowIf(inputStream == null, ErrorCode.USER_ERROR_A0700);
        String key = generateKey(rootPath, suffix);
        byte[] dataBytes;

        try {
            dataBytes = inputStream.readAllBytes();
        } catch (IOException e) {
            log.error("读取输入流失败: {}", e.getMessage());
            throw new ServiceException(ErrorCode.SYSTEM_ERROR_B0001, e.getMessage());
        }

        COSClient cosClient = tencentConfiguration.createCosClient();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(dataBytes.length);
            PutObjectRequest putObjectRequest = new PutObjectRequest(tencentProperties.getBucketName(), key, new ByteArrayInputStream(dataBytes), metadata);
            cosClient.putObject(putObjectRequest);
            log.info("文件上传成功，文件路径: {}", key);
            return key;
        } catch (Exception e) {
            log.error("文件上传到COS失败: {}", e.getMessage());
            throw new RemoteServiceException(ErrorCode.SERVICE_ERROR_C0001, e.getMessage());
        } finally {
            cosClient.shutdown();
        }
    }

    /**
     * 删除COS中的文件
     *
     * @param fileKey 文件的COS路径
     */
    public void deleteFromCos(String fileKey) {
        // 校验文件的COS路径
        ThrowUtils.clientExceptionThrowIf(StringUtils.isBlank(fileKey), ErrorCode.USER_ERROR_A0700);
        COSClient cosClient = tencentConfiguration.createCosClient();
        try {
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(tencentProperties.getBucketName()).withKeys(fileKey);
            DeleteObjectsResult deleteObjectsResult = cosClient.deleteObjects(deleteObjectsRequest);
            log.info("成功删除文件: {}", deleteObjectsResult.getDeletedObjects());
        } catch (Exception e) {
            log.error("删除文件失败: {}", e.getMessage());
            throw new RemoteServiceException(ErrorCode.SERVICE_ERROR_C0001, e.getMessage());
        } finally {
            cosClient.shutdown();
        }
    }

    /**
     * 列出指定路径下的文件
     *
     * @param rootPath 上传的路径
     * @return 文件列表
     */
    public List<String> listFiles(String rootPath) {
        // 校验路径是否为空
        ThrowUtils.clientExceptionThrowIf(StringUtils.isBlank(rootPath), ErrorCode.USER_ERROR_A0700);

        COSClient cosClient = tencentConfiguration.createCosClient();
        List<String> fileList;

        try {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                    .withBucketName(tencentProperties.getBucketName())
                    .withPrefix(rootPath + "/");
            ObjectListing listObjectsResult = cosClient.listObjects(listObjectsRequest);
            fileList = listObjectsResult.getObjectSummaries()
                    .stream()
                    .map(COSObjectSummary::getKey)
                    .collect(Collectors.toList());
            log.info("列出文件成功: {}", fileList);
        } catch (Exception e) {
            log.error("列出文件失败: {}", e.getMessage());
            throw new RemoteServiceException(ErrorCode.SERVICE_ERROR_C0001, e.getMessage());
        } finally {
            cosClient.shutdown();
        }
        return fileList;
    }

    // 获取文件后缀
    private String getFileSuffix(String originalName) {
        return StringUtils.substringAfterLast(originalName, ".");
    }

    // 生成上传的文件路径
    private String generateKey(String rootPath, String suffix) {
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + System.currentTimeMillis() + "." + suffix;
        return StringUtils.isBlank(rootPath) ? fileName : rootPath + "/" + fileName;
    }
}
