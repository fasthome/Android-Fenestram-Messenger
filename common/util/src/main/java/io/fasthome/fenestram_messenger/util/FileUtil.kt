/**
 * Created by Dmitry Popov on 22.04.2022.
 */
package io.fasthome.fenestram_messenger.util

import android.content.ContentResolver
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

fun createFile(parent : File, fileName : String) : File {
    val file = File(parent, fileName)
    if (!file.parentFile.exists())
        file.parentFile.mkdirs()
    if (!file.exists())
        file.createNewFile();
    return file
}

fun fileSizeInMb(size : Long) = size / 1048576f

fun fileSizeInKb(size : Long) = size / 1024f

fun fileSizeInKbFromMb(size : Float) = size * 1024f

/**
 * Получения [PrintableText] если [size] в мегабайтах
 */
fun getPrettySize(size : Float?): PrintableText {
    if(size == null) return PrintableText.StringResource(R.string.kb_real, 0f)
    return if(size >= 1.0f){
        PrintableText.StringResource(R.string.mb_real, size)
    }else{
        PrintableText.StringResource(R.string.kb_real, fileSizeInKbFromMb(size))
    }
}

/**
 * Получение [PrintableText] если [size] в килобайтах
 */
fun getPrettySize(size : Long?): PrintableText {
    if(size == null) return PrintableText.StringResource(R.string.mb_real, 0)
    return if(fileSizeInMb(size) >= 1.0f){
        val sizeMb = fileSizeInMb(size)
        PrintableText.StringResource(R.string.mb_real, sizeMb)
    }else{
        val sizeKb = fileSizeInKb(size)
        PrintableText.StringResource(R.string.kb_real, sizeKb)
    }
}

fun getLoadedFileSize(loadedSize : Float, wholeSize : Float) : PrintableText{
    return if(wholeSize >= 1.0f){
        PrintableText.StringResource(R.string.meta_document_size_ph_mb, loadedSize, wholeSize)
    }else{
        PrintableText.StringResource(R.string.meta_document_size_ph_kb, loadedSize, wholeSize)
    }
}


fun getLoadedFileSize(loadedSize : Long, fullSize : Long) : PrintableText{
    return if(fileSizeInMb(fullSize) >= 1){
        PrintableText.StringResource(R.string.meta_document_size_ph_mb, fileSizeInMb(loadedSize), fileSizeInMb(fullSize))
    }else{
        PrintableText.StringResource(R.string.meta_document_size_ph_kb, fileSizeInKb(loadedSize), fileSizeInKb(fullSize))
    }
}



fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
    inputStream.use { input ->
        val outputStream = FileOutputStream(outputFile)
        outputStream.use { output ->
            val buffer = ByteArray(4 * 1024) // buffer size
            while (true) {
                val byteCount = input.read(buffer)
                if (byteCount < 0) break
                output.write(buffer, 0, byteCount)
            }
            output.flush()
        }
    }
}

@Throws(IOException::class)
fun readBytes(contentResolver: ContentResolver, uri: Uri): ByteArray? =
    contentResolver.openInputStream(uri)?.buffered()?.use { it.readBytes() }