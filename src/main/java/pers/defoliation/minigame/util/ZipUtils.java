package pers.defoliation.minigame.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public interface ZipUtils {
	static boolean zip(File zipFile, File... inputFiles) throws IOException  {
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
			for (File file : inputFiles) {
				if (file.isDirectory())
					zip(out, file, "");
				else
					zip(out, file, file.getName());
			}
			return true;
		}
	}

	static boolean zip(Path zipFile, Path... inputFiles) throws IOException {
		try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            for (Path file : inputFiles) {
                if (Files.isDirectory(file))
                    zip(out, file, "");
                else
                    zip(out, file, file.toString());
            }
		    return true;
		}
	}

    static void zip(ZipOutputStream out, Path f, String base) throws IOException {
        if (Files.isDirectory(f)) {
            if (!base.isEmpty()) {
                out.putNextEntry(new ZipEntry(base + "/"));
                base = base + "/";
            }
            String finalBase = base;
            for (Path f1: Files.list(f).collect(Collectors.toList())) // 递归遍历子文件夹
                zip(out, f1, finalBase + f1.toString());
        } else {
            out.putNextEntry(new ZipEntry(base)); // 创建zip压缩进入点base
            try (InputStream in = Files.newInputStream(f)){
                int b;
                while ((b = in.read()) != -1)
                    out.write(b); // 将字节流写入当前zip目录
            }
        }
    }

	static void zip(ZipOutputStream out, File f, String base) throws IOException {
		if (f.isDirectory()) {
			if (!base.isEmpty()) {
				out.putNextEntry(new ZipEntry(base + "/"));
				base = base + "/";
			}
			for (File f1:f.listFiles()) {
				zip(out, f1, base + f1.getName()); // 递归遍历子文件夹
			}
		} else {
			out.putNextEntry(new ZipEntry(base)); // 创建zip压缩进入点base
			try(FileInputStream in = new FileInputStream(f)){
				int b;
				while ((b = in.read()) != -1)
					out.write(b); // 将字节流写入当前zip目录
			}
		}
	}

}
