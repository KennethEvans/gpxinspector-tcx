package net.kenevans.gpxinspector.tcx.converters;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;

import net.kenevans.gpxinspector.converters.IGpxConverter;
import net.kenevans.gpxinspector.utils.SWTUtils;
import net.kenevans.gpxtrackpointextensionv2.ExtensionsType;
import net.kenevans.gpxtrackpointextensionv2.GpxType;
import net.kenevans.gpxtrackpointextensionv2.TrackPointExtensionT;
import net.kenevans.gpxtrackpointextensionv2.TrkType;
import net.kenevans.gpxtrackpointextensionv2.TrksegType;
import net.kenevans.gpxtrackpointextensionv2.WptType;
import net.kenevans.gpxtrackpointextensionv2.parser.GPXParser;
import net.kenevans.trainingcenterdatabasev2.ActivityLapT;
import net.kenevans.trainingcenterdatabasev2.ActivityListT;
import net.kenevans.trainingcenterdatabasev2.ActivityT;
import net.kenevans.trainingcenterdatabasev2.HeartRateInBeatsPerMinuteT;
import net.kenevans.trainingcenterdatabasev2.PositionT;
import net.kenevans.trainingcenterdatabasev2.TrackT;
import net.kenevans.trainingcenterdatabasev2.TrackpointT;
import net.kenevans.trainingcenterdatabasev2.TrainingCenterDatabaseT;
import net.kenevans.trainingcenterdatabasev2.parser.TCXParser;

/*
 * Created on May 12, 2011
 * By Kenneth Evans, Jr.
 */

public class TcxConverter implements IGpxConverter {
	private static final String[] extensions = { ".tcx" };

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.kenevans.gpxinspector.converters.IGpxConverter#getFilterExtensions()
	 */
	@Override
	public String getFilterExtensions() {
		String retVal = "";
		for (String ext : extensions) {
			if (retVal.length() > 0) {
				retVal += ";";
			}
			retVal += "*" + ext;
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.kenevans.gpxinspector.converters.IGpxConverter#getPreferredExtension()
	 */
	@Override
	public String getPreferredExtension() {
		return extensions[0].replaceFirst("\\.", "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.kenevans.gpxinspector.converters.IGpxConverter#isReadSupported(java
	 * .lang.String)
	 */
	@Override
	public boolean isParseSupported(File file) {
		String fileExt = "." + SWTUtils.getExtension(file);
		if (fileExt != null) {
			for (String ext : extensions) {
				if (fileExt.equalsIgnoreCase(ext)) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.kenevans.gpxinspector.converters.IGpxConverter#isWriteSupported(java
	 * .lang.String)
	 */
	@Override
	public boolean isSaveSupported(File file) {
//        String fileExt = "." + SWTUtils.getExtension(file);
//        if(fileExt != null) {
//            for(String ext : extensions) {
//                if(fileExt.equalsIgnoreCase(ext)) {
//                    return true;
//                }
//            }
//        }
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.kenevans.gpxinspector.converters.IGpxConverter#parse(java.io.File)
	 */
	@Override
	public GpxType parse(File file) throws Throwable {
		TrainingCenterDatabaseT tcx = null;
		try {
			tcx = TCXParser.parse(file);
		} catch (JAXBException ex) {
			SWTUtils.errMsg("Failed to read " + file.getPath());
			return null;
		}

		return TCXParser.convertTCXtoGpx(tcx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.kenevans.gpxinspector.converters.IGpxConverter#save(net.kenevans.
	 * gpx.GpxType, java.io.File)
	 */
	@Override
	public void save(String creator, GpxType gpxType, File file) throws Throwable {
	}

}
