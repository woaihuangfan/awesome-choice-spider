package com.fan

import com.google.gson.Gson
import java.io.File

object JsonFileReader {
    private val gson = Gson()

    /**
     * 从测试资源的JSON文件中读取内容并转换为指定类型的对象
     *
     * @param fileName JSON文件名，不包括路径，例如 "test.json"
     * @param clazz 类型的Class对象
     * @return 转换后的对象
     */
    fun <T> readJsonFileFromResources(
        fileName: String,
        clazz: Class<T>,
    ): T {
        val fileContent = readFileContentFromResources(fileName)
        return gson.fromJson(fileContent, clazz)
    }

    /**
     * 从测试资源的文件中读取内容
     *
     * @param fileName 文件名，不包括路径，例如 "test.json"
     * @return 文件内容
     */
    fun readFileContentFromResources(fileName: String): String {
        val classLoader = javaClass.classLoader
        val resource = classLoader.getResource(fileName)
        checkNotNull(resource) { "Resource $fileName not found" }

        val file = File(resource.file)
        return file.readText()
    }
}
