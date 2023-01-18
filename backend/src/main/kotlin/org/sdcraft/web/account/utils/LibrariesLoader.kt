package org.sdcraft.web.account.utils

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.util.DigestUtils
import org.w3c.dom.Document
import org.xml.sax.SAXException
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException


class LibrariesLoader {
    private var logger: Logger = LogManager.getLogger("DependentLoader")
    @Throws(IOException::class)
    private fun downloadFile(file: File, url: URL) {
        url.openStream().use { `is` -> Files.copy(`is`, file.toPath()) }
    }
    /**
     * 从Maven仓库下载依赖
     *
     * @param groupId    组ID
     * @param artifactId 构建ID
     * @param version    版本
     * @param repo       仓库地址
     * @param extra      额外参数
     * @param file       保存文件
     * @param checkMD5   是否检查MD5
     * @return 下载成功返回true，否则返回false
     */
    @Throws(RuntimeException::class, IOException::class)
    fun downloadLibraryMaven(
        groupId: String,
        artifactId: String,
        version: String,
        extra: String?,
        repo: String,
        file: File,
        checkMD5: Boolean
    ): Boolean {
        // 创建文件夹
        var repo = repo
        if (!file.parentFile.exists() && !file.parentFile.mkdirs()) throw RuntimeException("Failed to create " + file.parentFile.path)
        // 下载地址格式
        if (!repo.endsWith("/")) repo += "/"
        repo += "%s/%s/%s/%s-%s%s.jar"
        val downloadURL =
            String.format(repo, groupId.replace(".", "/"), artifactId, version, artifactId, version, extra) // 下载地址
        val fileName = "$artifactId-$version.jar" // 文件名

        // 检查MD5
        if (checkMD5) {
            val fileMD5 = File(file.parentFile, "$fileName.md5")
            val downloadMD5Url = "$downloadURL.md5"
            val downloadMD5UrlFormat = URL(downloadMD5Url)
            if (fileMD5.exists() && !fileMD5.delete()) throw RuntimeException("Failed to delete " + fileMD5.path)
            downloadFile(fileMD5, downloadMD5UrlFormat) // 下载MD5文件
            if (!fileMD5.exists()) throw RuntimeException("Failed to download $downloadMD5Url")
            if (file.exists()) {
                val fis = FileInputStream(file)
                Files.readAllBytes(fileMD5.toPath())
                val isSame: Boolean = DigestUtils.md5DigestAsHex(fis) == Files.readAllBytes(fileMD5.toPath()).toString(Charsets.UTF_8)
                if (!isSame) {
                    fis.close()
                    if (!file.delete()) throw RuntimeException("Failed to delete " + file.path)
                }
            }
        } else if (file.exists() && !file.delete()) { // 不检查直接删原文件下新的
            throw RuntimeException("Failed to delete " + file.path)
        }

        // 下载正式文件
        if (!file.exists()) {
            logger.info("Downloading $downloadURL")
            downloadFile(file, URL(downloadURL))
        }
        return file.exists()
    }

    /**
     * @param groupId    组ID
     * @param artifactId 构件ID
     * @param repoUrl    仓库地址
     * @param xmlTag     XML标签
     * @return 版本名
     */
    @Throws(RuntimeException::class, IOException::class, ParserConfigurationException::class, SAXException::class)
    fun getLibraryVersionMaven(groupId: String, artifactId: String, repoUrl: String, xmlTag: String?): String? {
        var repoUrl = repoUrl
        val cacheDir = File(System.getProperty("user.dir"), "cache")
        if (!cacheDir.exists() && !cacheDir.mkdirs()) throw RuntimeException("Failed to create " + cacheDir.path)
        val metaFileName = "maven-metadata-$groupId.$artifactId.xml"
        val metaFile = File(cacheDir, metaFileName)
        if (!repoUrl.endsWith("/")) repoUrl += "/"
        repoUrl += "%s/%s/" // 根目录格式
        val repoFormat = String.format(repoUrl, groupId.replace(".", "/"), artifactId) // 格式化后的根目录

        // MD5
        val metaFileMD5 = File(cacheDir, "$metaFileName.md5")
        if (metaFileMD5.exists() && !metaFileMD5.delete()) throw RuntimeException("Failed to delete " + metaFileMD5.path)
        val metaFileMD5Url = URL(repoFormat + "maven-metadata.xml.md5")
        downloadFile(metaFileMD5, metaFileMD5Url)
        if (!metaFileMD5.exists()) throw RuntimeException("Failed to download $metaFileMD5Url")

        // 验证meta文件
        logger.info("Verifying $metaFileName")
        if (metaFile.exists()) {
            FileInputStream(metaFile).use { fis ->
                if (DigestUtils.md5DigestAsHex(fis) != Files.readAllBytes(metaFileMD5.toPath()).toString(Charsets.UTF_8)
                ) {
                    fis.close()
                    if (!metaFile.delete()) throw RuntimeException("Failed to delete " + metaFile.path)
                    val metaFileUrl = URL(repoFormat + "maven-metadata.xml")
                    downloadFile(metaFile, metaFileUrl)
                    if (!metaFileMD5.exists()) throw RuntimeException("Failed to download $metaFileUrl")
                }
            }
        } else {
            val metaFileUrl = URL(repoFormat + "maven-metadata.xml")
            logger.info("Downloading $metaFileUrl")
            downloadFile(metaFile, metaFileUrl)
            if (!metaFileMD5.exists()) throw RuntimeException("Failed to download $metaFileUrl")
        }

        // 读取内容
        val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
        val builder: DocumentBuilder = factory.newDocumentBuilder()
        val doc: Document = builder.parse(metaFile)
        return doc.getElementsByTagName(xmlTag).item(0).firstChild.nodeValue
    }

    /**
     * 加载 Maven 仓库的依赖库
     *
     * @param groupId    组ID
     * @param artifactId 构件ID
     * @param version    版本
     * @param repo       仓库地址
     * @param extra      额外参数
     * @param path       保存目录
     */
    @Throws(RuntimeException::class, IOException::class)
    fun loadLibraryClassMaven(
        groupId: String,
        artifactId: String,
        version: String,
        extra: String?,
        repo: String,
        path: File?
    ) {
        val name = "$artifactId-$version.jar" // 文件名

        // jar
        val saveLocation = File(path, File(File(groupId.replace(".","/"),artifactId),name).toString())
        logger.info("Verifying $name")
        if (!downloadLibraryMaven(groupId, artifactId, version, extra, repo, saveLocation, true)) {
            throw RuntimeException("Failed to download libraries!")
        }

        // -- 加载开始 --
        loadLibraryClassLocal(saveLocation)
    }

    /**
     * 加载本地 Jar
     *
     * @param file Jar 文件
     */
    @Throws(IOException::class)
    fun loadLibraryClassLocal(file: File) {
        logger.info("Loading library $file")
        val classLoader = ClassLoader.getSystemClassLoader()
        val url: URL = file.toURI().toURL()
        if (classLoader is URLClassLoader) {
            //Java 8
            val sysLoader = ClassLoader.getSystemClassLoader() as URLClassLoader
            val sysClass = URLClassLoader::class.java
            try {
                val method: Method = sysClass.getDeclaredMethod("addURL", URL::class.java)
                method.isAccessible = true
                method.invoke(sysLoader, url)
            } catch (var5: Exception) {
                var5.printStackTrace()
                throw IllegalStateException(var5.message, var5)
            }
        } else {
            try {
                val field: Field = try {
                    // Java 9 - 15
                    classLoader.javaClass.getDeclaredField("ucp")
                } catch (e: NoSuchFieldException) {
                    // Java 16+
                    classLoader.javaClass.superclass.getDeclaredField("ucp")
                }
                field.isAccessible = true
                val ucp: Any = field.get(classLoader)
                val method: Method = ucp.javaClass.getDeclaredMethod("addURL", URL::class.java)
                method.isAccessible = true
                method.invoke(ucp, url)
            } catch (exception: Exception) {
                exception.printStackTrace()
                throw IllegalStateException(exception.message, exception)
            }
        }
        logger.info("Library $file Loaded")
    }
}