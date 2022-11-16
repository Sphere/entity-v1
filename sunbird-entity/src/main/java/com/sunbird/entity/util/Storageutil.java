package com.sunbird.entity.util;

import java.io.File;
import java.io.FileOutputStream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.sunbird.cloud.storage.BaseStorageService;
import org.sunbird.cloud.storage.factory.StorageConfig;
import org.sunbird.cloud.storage.factory.StorageServiceFactory;

import scala.Option;

public class Storageutil {

	public static final Logger LOGGER = LoggerFactory.getLogger(Storageutil.class);

	@Autowired
	static ServerProperties serverProperties;

	private static BaseStorageService storageService = null;

	@PostConstruct

	public void init() {
		if (storageService == null) {
			storageService = StorageServiceFactory.getStorageService(new StorageConfig(serverProperties.getTypeName(),
					serverProperties.getIdentityName(), serverProperties.getStorageKey()));
		}
	}

	public static Boolean uploadFile(MultipartFile mFile) {
		try {
			File file = new File(System.currentTimeMillis() + "_" + mFile.getOriginalFilename());
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(mFile.getBytes());
			fos.close();
			String objectKey = serverProperties.getContainerName() + "/" + file.getName();
			String url = storageService.upload(serverProperties.getContainerName(), file.getAbsolutePath(), objectKey,
					Option.apply(false), Option.apply(1), Option.apply(5), Option.empty());
			file.delete();
			if (url != null) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.Exception.EXCEPTION_METHOD, "uploadFile", e.getMessage()));
		}
		return false;
	}

	public static Boolean deleteFile(String fileName) {
		try {
			storageService.deleteObject(serverProperties.getContainerName(), fileName, Option.apply(Boolean.FALSE));
			return true;
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.Exception.EXCEPTION_METHOD, "deleteFile", e.getMessage()));
		}
		return false;
	}
}
