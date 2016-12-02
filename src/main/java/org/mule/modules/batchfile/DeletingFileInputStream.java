package org.mule.modules.batchfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DeletingFileInputStream extends FileInputStream {

	private File file;
	private boolean delete;

	public DeletingFileInputStream(String fileName) throws FileNotFoundException{
	      this(new File(fileName), false);
	   }

	public DeletingFileInputStream(File file, boolean delete) throws FileNotFoundException{
	      super(file);
	      this.file = file;
	      this.delete = delete;
	   }

	public void close() throws IOException {
		try {
			super.close();
		} finally {
			if (file != null && delete) {
				file.delete();
				file = null;
			}
		}
	}

}
