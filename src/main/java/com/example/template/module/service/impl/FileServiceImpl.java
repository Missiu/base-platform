package com.example.template.module.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.template.module.domain.entity.File;
import com.example.template.module.mapper.FileMapper;
import com.example.template.module.service.FileService;
import org.springframework.stereotype.Service;

/**
* @author hzh
* @description 针对表【t_file(文件表)】的数据库操作Service实现
* @createDate 2024-10-16 21:27:52
*/
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File>
    implements FileService{

}




