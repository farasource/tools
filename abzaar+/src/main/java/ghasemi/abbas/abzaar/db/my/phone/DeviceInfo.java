package ghasemi.abbas.abzaar.db.my.phone;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.File;
import java.util.regex.Pattern;

public class DeviceInfo {

	private static Context context;
	private static final Pattern DIR_SEPORATOR = Pattern.compile("/");

	public DeviceInfo(Context context) {
		this.context = context;
	}

	public static boolean externalMemoryAvailable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return (availableBlocks * blockSize);
	}

	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return (totalBlocks * blockSize);
	}

	public static String getFreeInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long total = (long) stat.getBlockCount() * (long) stat.getBlockSize();
		long available = (long) stat.getAvailableBlocks()
				* (long) stat.getBlockSize();
		long free = Math.abs(total - available);
		return Formatter.formatFileSize(context, free);
	}

	public static long getAvailableExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return (availableBlocks * blockSize);
		} else {
			return 0L;
		}
	}

	public static long getTotalExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return (totalBlocks * blockSize);
		} else {
			return 0L;
		}
	}

	public static String getFreeExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long total = (long) stat.getBlockCount() * (long) stat.getBlockSize();
			long available = (long) stat.getAvailableBlocks()
					* (long) stat.getBlockSize();
			long free = Math.abs(total - available);
			return Formatter.formatFileSize(context, free);
		} else {
			return "Error";
		}
	}

	public static String formatMemSize(long size, int value) {
		String result = "";
		if (1024L > size) {
			String info = String.valueOf(size);
			result = (new StringBuilder(info)).append(" B").toString();
		} else if (1048576L > size) {
			String s2 = (new StringBuilder("%.")).append(value).append("f")
					.toString();
			Object aobj[] = new Object[1];
			Float float1 = Float.valueOf((float) size / 1024F);
			aobj[0] = float1;
			String s3 = String.valueOf(String.format(s2, aobj));
			result = (new StringBuilder(s3)).append(" KB").toString();
		} else if (1073741824L > size) {
			String s4 = (new StringBuilder("%.")).append(value).append("f")
					.toString();
			Object aobj1[] = new Object[1];
			Float float2 = Float.valueOf((float) size / 1048576F);
			aobj1[0] = float2;
			String s5 = String.valueOf(String.format(s4, aobj1));
			result = (new StringBuilder(s5)).append(" MB").toString();
		} else {
			Object aobj2[] = new Object[1];
			Float float3 = Float.valueOf((float) size / 1.073742E+009F);
			aobj2[0] = float3;
			String s6 = String.valueOf(String.format("%.2f", aobj2));
			result = (new StringBuilder(s6)).append(" GB").toString();
		}
		return result;
	}

}
