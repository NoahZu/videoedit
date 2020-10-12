package io.github.noahzu.videoedit.ext

import java.io.File
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

fun String.md5(): String {
    try {
        val instance: MessageDigest = MessageDigest.getInstance("MD5")//获取md5加密对象
        val digest: ByteArray = instance.digest(this.toByteArray())//对字符串加密，返回字节数组
        val sb = StringBuffer()
        for (b in digest) {
            val i: Int = b.toInt() and 0xff//获取低八位有效值
            var hexString = Integer.toHexString(i)//将整数转化为16进制
            if (hexString.length < 2) {
                hexString = "0$hexString"//如果是一位的话，补0
            }
            sb.append(hexString)
        }
        return sb.toString()

    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return this
}



fun ByteArray.md5(): String? {
    try {
        val instance: MessageDigest = MessageDigest.getInstance("MD5")//获取md5加密对象
        val digest: ByteArray = instance.digest(this)//对字符串加密，返回字节数组
        val sb = StringBuffer()
        for (b in digest) {
            val i: Int = b.toInt() and 0xff//获取低八位有效值
            var hexString = Integer.toHexString(i)//将整数转化为16进制
            if (hexString.length < 2) {
                hexString = "0$hexString"//如果是一位的话，补0
            }
            sb.append(hexString)
        }
        return sb.toString()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return null
}

fun String.toFile(): File {
    return File(this)
}

fun  String?.myToLong(default:Long):Long{
    return if(this.isNullOrEmpty()){
        default
    }else{
        try {
            this.toLong()
        }catch (e:Exception){
            default
        }
    }
}
fun  String?.myToInt(default:Int):Int{
    return if(this.isNullOrEmpty()){
        default
    }else{
        try {
            this.toInt()
        }catch (e:Exception){
            default
        }
    }
}

fun  String?.myToFloat(default:Float):Float {
    return if (this.isNullOrEmpty()) {
        default
    } else {
        try {
            this.toFloat()
        } catch (e: Exception) {
            default
        }
    }
}
fun String?.myToDouble(default: Double): Double {
    return if (this.isNullOrEmpty()) {
        default
    } else {
        try {
            this.toDouble()
        } catch (e: Exception) {
            default
        }
    }
}


fun Float?.myFormatDecimalPlace(default: Int = 2,defaultValue:Float = 0.0f): Float {
    return if (this==null) {
        defaultValue
    } else {
        try {
            return String.format("%.${default}f",this).myToFloat(0.0f)
        } catch (e: Exception) {
            defaultValue
        }
    }
}

fun Double?.myFormatDecimalPlace(default: Int = 2,defaultValue:Double = 0.0): Double {
    return if (this==null) {
        defaultValue
    } else {
        try {
            return String.format("%.${default}f",this).myToDouble(0.0)
        } catch (e: Exception) {
            defaultValue
        }
    }
}

fun Float.toStringFloat(floatNum:Int=2):String{
    return String.format("%.${floatNum}f",this)
}




