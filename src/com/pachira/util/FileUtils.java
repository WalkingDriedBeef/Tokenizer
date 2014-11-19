package com.pachira.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class FileUtils{
	/**
	 * 写文件
	 * @param path
	 * @param sent
	 */
	public static void writefile(String path, String textName , String sent) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(path+"/" + textName, true);
			Writer out = new OutputStreamWriter(fos, "utf8");
			out.write(sent+System.getProperty("line.separator"));
			out.flush();
			out.close();
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 按照GBK格式写文件
	 * @param path
	 * @param textName
	 * @param sent
	 */
	public static void writefileByGBK(String path, String textName , String sent) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(path+"/" + textName, true);
			Writer out = new OutputStreamWriter(fos, "gbk");
			out.write(sent+System.getProperty("line.separator"));
			out.flush();
			out.close();
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 按照编码格式读取文件
	 * @param filename
	 * @param charset
	 * @return
	 */
	public static List<String> readFile(String filename,String charset) {
		List<String> list = new ArrayList<String>();
		try {
			java.util.Scanner in = new java.util.Scanner(new FileInputStream(filename),charset);
			while(in.hasNext()) {
				String line = in.nextLine().trim();
				list.add(line);
			}
			in.close();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获得资源的路径
	 * @param cls
	 * @param path
	 * @return
	 */
	public static String getJarPath(@SuppressWarnings("rawtypes") Class cls,String path){
		String url = cls.getResource(path).toString();
		url = url.substring(url.indexOf("file:/")+"file:/".length());
		return url;
	}
	/**
	 * 获得当前类的路径
	 * @param cls
	 * @return
	 */
	public static String getOSPath(@SuppressWarnings("rawtypes") Class cls) {
		String path = getAppPath(cls);
		if (StringUtils.isEmpty(path)) {
			return "";
		}
		if(isWindows()) {
			return path.substring(1).replace("/", "\\");
		}else  {
			return path;
		}
	}
	/**
	 * 判断程序运行在什么平台上(win or linux)
	 * @return
	 */
	public static boolean isWindows(){
		if(System.getProperty("os.name").toUpperCase().indexOf("LINUX") != -1) {
			return false;
		}else {
			return true;
		}
	}
	public static String getAppPath(@SuppressWarnings("rawtypes") Class cls) {
		if (cls == null)
			throw new java.lang.IllegalArgumentException("parameter is null");
		ClassLoader loader = cls.getClassLoader();
		String clsName = cls.getName() + ".class";
		Package pack = cls.getPackage();
		String path = "";
		// 如果不是匿名包，将包名转化为路径
		if (pack != null) {
			String packName = pack.getName();
			// 此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
			if (packName.startsWith("java.") || packName.startsWith("javax."))
				throw new java.lang.IllegalArgumentException(
						"don't pass system class");
			// 在类的名称中，去掉包名的部分，获得类的文件名
			clsName = clsName.substring(packName.length() + 1);
			// 判定包名是否是简单包名，如果是，则直接将包名转换为路径，
			if (packName.indexOf(".") < 0)
				path = packName + "/";
			else {// 否则按照包名的组成部分，将包名转换为路径
				int start = 0, end = 0;
				end = packName.indexOf(".");
				while (end != -1) {
					path = path + packName.substring(start, end) + "/";
					start = end + 1;
					end = packName.indexOf(".", start);
				}
				path = path + packName.substring(start) + "/";
			}
		}
		// 调用ClassLoader的getResource方法，传入包含路径信息的类文件名
		java.net.URL url = loader.getResource(path + clsName);
		// 从URL对象中获取路径信息
		String realPath = url.getPath();
		// 去掉路径信息中的协议名"file:"
		int pos = realPath.indexOf("file:");
		if (pos > -1)
			realPath = realPath.substring(pos + 5);
		// 去掉路径信息最后包含类文件信息的部分，得到类所在的路径
		pos = realPath.indexOf(path + clsName);
		realPath = realPath.substring(0, pos - 1);
		// 如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
		if (realPath.endsWith("!"))
			realPath = realPath.substring(0, realPath.lastIndexOf("/"));
		try {
			realPath = java.net.URLDecoder.decode(realPath, "utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return realPath;
	}
	/**
	 * 获得文件的路径
	 * @param filepath
	 * @return
	 */
	public static String getDirpath(String filepath){
		try {
			File f = new File(filepath);
			if(f.exists()&&f.isFile()){
				String s = f.getAbsolutePath();
				return s.substring(0, s.lastIndexOf(File.separator));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	/**
	 * 获得文件的名称
	 * @param filepath
	 * @return
	 */
	public static String getName(String filepath){
		try {
			File f = new File(filepath);
			if(f.exists()&&f.isFile()){
				return f.getName();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	/**
	 * 创建文件夹
	 * @param path
	 * @return
	 */
	public static boolean mkdir(String path){
		boolean make = true;
		try{
			File dic = new File(path);
			if(dic.exists()){
				for(File f : dic.listFiles()){
					if(f.isFile()){
						f.delete();
					}
				}
			}else{
				return dic.mkdirs();
			}
		}catch(Exception e){
			System.err.println("File Directory create error:"+e.getMessage());
			make = false;
		}
		return make;
	}
	/**
	 * 复制一个文件
	 * @param file
	 * @param targetFile
	 * @return
	 */
	public static boolean copyFile(String file, String targetFile) {
		try {
			FileInputStream in = new FileInputStream(file);
			FileOutputStream out = new FileOutputStream(targetFile, true);
			byte[] b = new byte[1024 * 3];
			int len;
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			out.flush();
			out.close();
			in.close();
			return true;
		} catch (Exception e) {
			System.err.println("copyfile error:"+e.getMessage());
		}
		return false;
	}
	/**
	 * 判断文件是否存在
	 * @param file
	 * @return
	 */
	public static boolean exists(String file){
		File f = new File(file);
		return f.exists();
	}
	/**
	 * 删除文件
	 * @param file
	 * @return
	 */
	public static boolean delFile(String file){
		File f = new File(file);
		return f.delete();
	}
	/**
	 * 获得上一级的文件夹的目录
	 * @param dirpath
	 * @return
	 */
	public static String getHigherDirpath(String dirpath){
		try {
			File f = new File(dirpath);
			if(f.exists()&&f.isDirectory()){
				String s = f.getAbsolutePath();
				return s.substring(0, s.lastIndexOf(File.separator));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
}
