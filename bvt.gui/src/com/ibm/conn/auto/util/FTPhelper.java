package com.ibm.conn.auto.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPhelper {

	protected static Logger log = LoggerFactory.getLogger(FTPhelper.class);
	
	/**
	 * Attempt to connect and login to a FTP server. On failure, closes and
	 * cleans up the instance of FtpClient.
	 * @param ftp - an instance of FtpClient
	 * @param hostname - the host name of the FTP server to connect to
	 * @param port - the port of the FTP server to connect to
	 * @param ftpUser - the user name to user when authenticating with the FTP server
	 * @param ftpPass - the password to user when authenticating with the FTP server
	 * @return true if the connection and login succeeded, false otherwise
	 */
	public static boolean connectAndLogin (FTPClient ftp, String hostname, int port, String ftpUser, String ftpPass) {
		
		InetAddress gridAddr = null;
		log.info("INFO: Looking up IP address for " + hostname);
		// Create an InetSocketAddress with the IP address of the grid
		// node and the port specified in the "ftp_port" parameter in
		// the testNG XML configuration
		try {
			gridAddr = InetAddress.getByName(hostname);
		} catch (UnknownHostException e) {
			log.info("INFO: Could not get the IP address of hostname: " + hostname + " !");
			return false;
		}

		// Try to connect to the grid node's FTP server
		try {
			log.info("INFO: Connecting to FTP server on " + hostname + ":" + port);
			ftp.connect(gridAddr, port);
			ftp.enterLocalPassiveMode();
		} catch (IOException ioe) {
			log.info("INFO: Could not connect to FTP server on " + hostname + ":" + port);
			return false;
		}

		// Try to log in to the grid node's FTP server
		try {
			log.info("INFO: Logging in to FTP server on " + hostname + ":" + port);

			if (ftpUser == null || ftpPass == null) {
				log.info("INFO: The FTP username and password have not been set!");
				ftp.disconnect();
				return false;
			}
			return ftp.login(ftpUser, ftpPass);
		} catch (IOException ioe) {
			log.info("INFO: Could not log in to FTP server on " + hostname + ":" + port);
			try {
				ftp.disconnect();
			} catch (IOException e) {}
			return false;
		}
	}
	
	/**
	 * Notify the FTP server not to do any line ending conversions on any files
	 * that are uploaded or downloaded. On failure, closes and cleans 
	 * up the instance of FtpClient.
	 * @param ftp - an instance of FtpClient
	 * @return true if binary mode was successfully set, false on failure
	 */
	public static boolean setBinaryType(FTPClient ftp) {
		// Try to set file transfers to binary mode 
		try {
			log.info("INFO: Setting file transfer mode to binary");
			ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);
		} catch (IOException ioe) {
			log.info("INFO: Could not set tranfers to binary mode on " + ftp.getRemoteAddress());
			try {
				ftp.disconnect();
			} catch (IOException e) {}
			return false;
		}
		return true;
	}
	
	/**
	 * Set the current working directory on the FTP server used for all 
	 * directory listings and file transfers. On failure, closes and cleans 
	 * up the instance of FtpClient.
	 * @param ftp - an instance of FtpClient
	 * @param directory - The directory to set the current working directory to
	 * @return true if the directory was successfully changed, false on failure
	 */
	public static boolean changeDirectory(FTPClient ftp, String directory) {
		try {
			log.info("INFO: Changing directory to " + directory +
					" on FTP server " + ftp.getRemoteAddress());
			return ftp.changeWorkingDirectory(directory);
		} catch (IOException ioe) {
			log.info("INFO: Could not change to directory " + directory +
					" on FTP server " + ftp.getRemoteAddress());
			try {
				ftp.disconnect();
			} catch (IOException e) {}
			return false;
		}
	}
	
	/**
	 * Retrieve a list of directory entries from the current directory on the
	 * FTP Server. The entries may be either files or sub directories. On 
	 * failure, closes and cleans up the instance of FtpClient.
	 * @param ftp - an instance of FtpClient
	 * @return a List of FTPFile objects on successs, null on failure
	 */
	public static List<FTPFile> listFiles(FTPClient ftp) {
		ArrayList<FTPFile> dirEntries = new ArrayList<FTPFile>();
		
		FTPFile[] dir = null;
		try {
			log.info("INFO: Reading contents of directory " + ftp.printWorkingDirectory() +
					" on FTP server " + ftp.getRemoteAddress());
			dir = ftp.listFiles();
		} catch (IOException e) {
			log.info("INFO: Could not read contents of directory " +
					"on FTP server " + ftp.getRemoteAddress());
			try {
				ftp.disconnect();
			} catch (IOException e2) {}
			return null;
		}
		
		dirEntries.addAll(Arrays.asList(dir));
		return dirEntries;
	}
	
	/**
	 * Upload a file to an FTP server. On failure, closes and cleans up the 
	 * instance of FtpClient.
	 * @param ftp - an instance of FtpClient
	 * @param sourceFile - a java.io.File representing the file to upload
	 * @param destFileName - The name to upload the file as, must not contain
	 * any directory path.
	 * @return true if the upload succeeded, false otherwise
	 */
	public static boolean putFile(FTPClient ftp, File sourceFile, String destFileName) {
		// Try to upload the copy of the file
		FileInputStream is = null;
		boolean putOK = false;
		try {
			log.info("INFO: Uploading file " + destFileName +
					" to FTP server " + ftp.getRemoteAddress());
			is = new FileInputStream(sourceFile);
			putOK = ftp.storeFile(destFileName, is);
			log.info("INFO: FTP upload of file " + destFileName + " completed.");
		} catch (IOException ioe) {
			log.info("INFO: Could not upload file " + destFileName +
					" to FTP server " + ftp.getRemoteAddress());
			try {
				     ftp.disconnect();
			 } catch (IOException e) {}
			return false;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {}
			}
		}
		return putOK;
	}

	/**
	 * Attempt to verify the contents of a zip file on an FTP server. On
	 * failure, closes and cleans up the instance of FtpClient.
	 * @param ftp - an instance of FtpClient
	 * @param zipFile - the name of the zip file on the server to verify, must
	 * not contain any directory path.
	 * @return A count of the number of entries in the zip file, or -1 if an
	 * error occurred.
	 */
	public static int verifyZipFile(FTPClient ftp, String zipFile) {
		int nEntries = 0;
		boolean downloadSuccess = false;
		
		// create a local temp file to write to
		try {
			File tmpDownloadFile = File.createTempFile("downloaded_files", ".zip");
			tmpDownloadFile.deleteOnExit();
			OutputStream downloadedFileOs = new BufferedOutputStream(new FileOutputStream(tmpDownloadFile));
			log.info("INFO: Dowloading file " + zipFile +
					" from FTP server " + ftp.getRemoteAddress() + " to " + tmpDownloadFile.getAbsolutePath());			
		
			// download the file
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			InputStream is = ftp.retrieveFileStream(zipFile);
			byte[] bytesArray = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = is.read(bytesArray)) != -1) {
				downloadedFileOs.write(bytesArray, 0, bytesRead);
			}
			
			downloadSuccess = ftp.completePendingCommand();
			if (downloadSuccess) {
				log.info(zipFile + " has been downloaded from FTP server " +ftp.getRemoteAddress());
				downloadedFileOs.close();
				is.close();

				try {
					// read the zip file
					log.info("Reading zip file :" + tmpDownloadFile.getAbsolutePath());
					ZipInputStream zis = new ZipInputStream(new FileInputStream(tmpDownloadFile));
					ZipEntry ze;

					while ((ze = zis.getNextEntry()) != null) {
						if (!ze.isDirectory()) {
							nEntries++;
						}
						log.info("INFO: Zip archive " + zipFile + " has a file: " + ze.getName());
						zis.closeEntry();
					}
					zis.close();
				} catch (IOException e) {
					log.info("INFO: Error " + e.getMessage() + " while reading contents of zip file: " +
							tmpDownloadFile.getAbsolutePath());
					nEntries = -1; // Cleanup comes after this
				}
			}
		} catch (IOException e) {
			log.error("INFO: Could not download file " + zipFile +
					" from FTP server " + ftp.getRemoteAddress() + ". Error: " + e.getMessage());
		} finally {
			try {
				ftp.disconnect();
			} catch (IOException e2) {
				log.error("INFO: Could not disconnect from FTP server " + ftp.getRemoteAddress());
				return -1;
			}
		}
		
		return nEntries;
	}

	
	/**
	 * Download a file from an FTP server. On failure, closes and cleans up the 
	 * instance of FtpClient.
	 * @param ftp - an instance of FtpClient
	 * @param sourceName - The name of the file on the server to download, must
	 * not contain any directory path.
	 * @param destFile - A java.io.File representing where the downloaded file
	 * will be saved to.
	 * @return true if the download succeeded, false otherwise
	 */
	public static boolean getFile (FTPClient ftp, String sourceName, File destFile) {
		// Try to upload the copy of the file
		FileOutputStream os = null;
		boolean getOK = false;
		try {
			log.info("INFO: Dowloading file " + sourceName +
					" from FTP server " + ftp.getRemoteAddress());
			os = new FileOutputStream(destFile);
			getOK = ftp.retrieveFile(sourceName, os);
			if (!getOK) {
				throw new IOException("File download failed!");
			}
			log.info("INFO: FTP download of file " + sourceName + " to local file " + 
					destFile.getPath() + "completed.");
		} catch (IOException ioe) {
			log.info("INFO: Could not download file " + sourceName +
					" from FTP server " + ftp.getRemoteAddress());
			try {
				ftp.disconnect();
				os.close();
			} catch (IOException e) {}
			return false;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {}
			}
		}
		return getOK;
	}
	
	/**
	 * Delete a file from an FTP server. On failure, closes and cleans up the 
	 * instance of FtpClient.
	 * @param ftp - an instance of FtpClient
	 * @param fileName - The name of the file on the server to delete, must
	 * not contain any directory path.
	 * @return true if the file was successfully deleted, false on failure
	 */
	public static boolean deleteFile (FTPClient ftp, String fileName) {
		boolean deleteOK = false;
		try {
			deleteOK = ftp.deleteFile(fileName);
			if (deleteOK) {
				log.info("INFO: Deleted file " + fileName + " from FTP server " +
						ftp.getRemoteAddress());
			} else {
				log.info("INFO: The file " + fileName + " was not deleted from the " +
						"FTP server " + ftp.getRemoteAddress());
			}
			return deleteOK;
		} catch (IOException ioe) {
			log.info("INFO: I/O error while deleting old temp file " + fileName +
					" on FTP server " + ftp.getRemoteAddress());
			try {
				ftp.disconnect();
			} catch (IOException e2) {}
			return false;
		}
	}
}
