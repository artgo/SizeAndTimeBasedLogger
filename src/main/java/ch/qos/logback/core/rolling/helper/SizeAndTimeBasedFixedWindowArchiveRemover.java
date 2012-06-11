package ch.qos.logback.core.rolling.helper;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.LiteralConverter;

public class SizeAndTimeBasedFixedWindowArchiveRemover extends SizeAndTimeBasedArchiveRemover {
	protected final static class DateFileComparator implements Comparator<File> {

		public int compare(final File f1, final File f2) {
			return (int) (f1.lastModified() - f2.lastModified());
		}

	}

	private final static DateFileComparator DATE_COMPARATOR = new DateFileComparator();

	private int maxHist = 0;

	public SizeAndTimeBasedFixedWindowArchiveRemover(FileNamePattern fileNamePattern, RollingCalendar rc) {
		super(fileNamePattern, rc);
	}

	public void clean(Date now) {
		super.clean(now);
		clenupForMaxFiles();
	}

	public void setMaxHistory(int maxHistory) {
		super.setMaxHistory(maxHistory);
		this.maxHist = maxHistory;
	}

	protected void clenupForMaxFiles() {
		final String regex = getRegex();
		final String stemRegex = FileFilterUtil.afterLastSlash(regex);
		File archive0 = new File(fileNamePattern.convertMultipleArguments(0));

		// in case the file has no directory part, i.e. if it's written into the
		// user's current directory.
		archive0 = archive0.getAbsoluteFile();

		final File parentDir = archive0.getAbsoluteFile().getParentFile();

		final File[] matchingFileArray = FileFilterUtil.filesInFolderMatchingStemRegex(parentDir, stemRegex);

		final List<File> sortedMatchingFileArray = sort(matchingFileArray);

		int filesToDelete = sortedMatchingFileArray.size() - maxHist;

		for (File f : sortedMatchingFileArray) {
			if (filesToDelete <= 0) {
				break;
			}
			f.delete();
			filesToDelete--;
		}

		if (parentClean) {
			removeFolderIfEmpty(parentDir);
		}
	}

	protected String getRegex() {
		final StringBuilder buf = new StringBuilder();
		Converter<Object> p = fileNamePattern.headTokenConverter;
		while (p != null) {
			if (p instanceof LiteralConverter) {
				buf.append(p.convert(null));
			} else if (p instanceof IntegerTokenConverter) {
				buf.append("\\d+");
			} else if (p instanceof DateTokenConverter) {
				DateTokenConverter<Object> dtc = (DateTokenConverter<Object>) p;
				buf.append(dtc.toRegex());
			}
			p = p.getNext();
		}
		return buf.toString();
	}

	private List<File> sort(File[] matchingFileArray) {
		final List<File> flist = Arrays.asList(matchingFileArray);
		
		Collections.sort(flist, DATE_COMPARATOR);
		
		return flist;
	}
}
