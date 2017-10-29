package com.gstore.service.impl;

import com.google.common.collect.Lists;
import com.gstore.service.IFileService;
import com.gstore.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;


/**
 * Created by Ocean on .
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        //扩张名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件，上传文件的文件名：{},上传路径:{},新文件名：{}",fileName,path,uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            //文件已上传成功

            FTPUtil.uploadFile(Lists.newArrayList(targetFile));//已经上传到ftp服务器上
            targetFile.delete();// 上传之后删除uplod下文件


        } catch (IOException e) {
            logger.error("上传文件异常",e);
            e.printStackTrace();
            return null;
        }
        return targetFile.getName();
    }

}
