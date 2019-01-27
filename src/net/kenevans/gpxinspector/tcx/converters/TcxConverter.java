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

		GpxType gpx = new GpxType();
		// These will be overwritten when saving as .gpx
		gpx.setCreator("TCX Converter for GPX Inspector");

		ActivityListT activityList;
		List<ActivityT> activities;
		List<ActivityLapT> laps;
		List<TrackT> tracks;
		List<TrackpointT> trackPoints;
		Short cad, hr;
		Double ele, dist;
		double lat, lon;
		XMLGregorianCalendar time;
		HeartRateInBeatsPerMinuteT hrBpm;
		PositionT position;

//		XMLGregorianCalendar id;
//		SensorStateT sensorState;
//		AbstractSourceT creator;
//		String creatorName, trainingPlanName, trainingPlanType;
//		TrainingT training;
//		PlanT plan;
//		TrainingTypeT trainingType;

		// GpxType variables (end with Gpx)
		TrkType trkTypeGpx = null;
		TrksegType trksegTypeGpx = null;
		WptType wptTypeGpx = null;
		TrackPointExtensionT tpExtGpx;
		ExtensionsType extTypeGpx;

		boolean error = false;
		activityList = tcx.getActivities();
		activities = activityList.getActivity();
		for (ActivityT activity : activities) {
//			id = activity.getId();
//			creator = activity.getCreator();
//			creatorName = creator.getName();

//			training = activity.getTraining();
//			trainingType = null;
//			trainingPlanName = null;
//			trainingPlanType = null;
//			if (training != null) {
//				plan = training.getPlan();
//				if (plan != null) {
//					trainingType = plan.getType();
//					trainingPlanName = plan.getName();
//					if (trainingType != null) {
//						trainingPlanType = trainingType.value();
//					}
//				}
//			}
			laps = activity.getLap();
			for (ActivityLapT lap : laps) {
				tracks = lap.getTrack();
				for (TrackT track : tracks) {
					trkTypeGpx = new TrkType();
					gpx.getTrk().add(trkTypeGpx);
					trksegTypeGpx = new TrksegType();
					trkTypeGpx.getTrkseg().add(trksegTypeGpx);
					trackPoints = track.getTrackpoint();
					for (TrackpointT trackPoint : trackPoints) {
						wptTypeGpx = new WptType();
						position = trackPoint.getPosition();
						if (position != null) {
							lat = position.getLatitudeDegrees();
							lon = position.getLongitudeDegrees();
							wptTypeGpx.setLat(BigDecimal.valueOf(lat));
							wptTypeGpx.setLon(BigDecimal.valueOf(lon));
							// Only add the segment if there is a valid lat and lon
							trksegTypeGpx.getTrkpt().add(wptTypeGpx);
						} else {
							continue;
						}

						ele = trackPoint.getAltitudeMeters();
						if (ele != null) {
							wptTypeGpx.setEle(BigDecimal.valueOf(ele));
						}

						time = trackPoint.getTime();
						if (time != null) {
							wptTypeGpx.setTime(time);
						}

						cad = trackPoint.getCadence();
						dist = trackPoint.getDistanceMeters();
						hrBpm = trackPoint.getHeartRateBpm();
						if (hrBpm != null) {
							hr = hrBpm.getValue();
						} else {
							hr = 0;
						}
						if ((hrBpm != null || cad != null || dist != null)) {
							tpExtGpx = new TrackPointExtensionT();
							if (hrBpm != null) {
								tpExtGpx.setHr(hr);
							}
							if (cad != null) {
								tpExtGpx.setCad(cad);
							}
							if (dist != null) {
								tpExtGpx.setDepth(dist);
							}
							extTypeGpx = new ExtensionsType();
							extTypeGpx.getAny().add(tpExtGpx);
							wptTypeGpx.setExtensions(extTypeGpx);
						}
					}
				}
			}
		}
		if (error) {
			return null;
		}
		return gpx;
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
