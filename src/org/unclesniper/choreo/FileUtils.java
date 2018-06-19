package org.unclesniper.choreo;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

public final class FileUtils {

	private FileUtils() {}

	public static File join(File base, File... tails) {
		for(File tail : tails) {
			if(tail.isAbsolute())
				base = tail;
			else
				base = new File(base, tail.getPath());
		}
		return base;
	}

	public static File strip(File whole, File prefix) {
		String wa = whole.getAbsolutePath(), pa = prefix.getAbsolutePath();
		if(wa.equals(pa))
			return new File(".");
		int palen = pa.length();
		boolean pHasSlash = !pa.isEmpty() && pa.charAt(palen - 1) == File.separatorChar;
		if(!wa.startsWith(pHasSlash ? pa : pa + File.separatorChar))
			return whole;
		int skip = palen;
		if(pHasSlash)
			++skip;
		return skip >= palen ? new File(".") : new File(wa.substring(skip));
	}

	public static boolean endsWith(File haystack, File needle) {
		String h = haystack.getPath(), n = needle.getPath();
		if(h.equals(n))
			return true;
		return h.endsWith(File.separatorChar + n);
	}

	public static File endsWithStrip(File haystack, File needle) {
		String h = haystack.getPath(), n = needle.getPath();
		if(h.equals(n))
			return null;
		if(!h.endsWith(File.separatorChar + n))
			return null;
		return new File(h.substring(0, h.length() - (n.length() + 1)));
	}

	public static void deleteRecursively(File file, boolean ifExists) throws IOException {
		FileUtils.deleteRecursively(file, ifExists, FileSystems.getDefault());
	}

	public static void deleteRecursively(File file, boolean ifExists, FileSystem vfs) throws IOException {
		if(file.isDirectory()) {
			File[] children = file.listFiles();
			if(children != null) {
				for(File child : children)
					FileUtils.deleteRecursively(child, ifExists, vfs);
			}
		}
		Path path = vfs.getPath(file.getPath());
		if(ifExists)
			Files.deleteIfExists(path);
		else
			Files.delete(path);
	}

}
