package com.gstore.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Ocean on .
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
