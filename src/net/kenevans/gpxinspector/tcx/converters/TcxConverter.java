package net.kenevans.gpxinspector.tcx.converters;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;

import net.kenevans.gpxinspector.converters.IGpxConverter;
import net.kenevans.gpxinspector.utils.SWTUtils;
import net.kenevans.gpxtrackpointextensionsv1.GpxType;
import net.kenevans.gpxtrackpointextensionsv1.TrkType;
import net.kenevans.gpxtrackpointextensionsv1.TrksegType;
import net.kenevans.gpxtrackpointextensionsv1.WptType;
import net.kenevans.gpxtrackpointextensionsv1.parser.GPXParser;
import net.kenevans.trainingcenterdatabasev2.AbstractSourceT;
import net.kenevans.trainingcenterdatabasev2.ActivityLapT;
import net.kenevans.trainingcenterdatabasev2.ActivityListT;
import net.kenevans.trainingcenterdatabasev2.ActivityT;
import net.kenevans.trainingcenterdatabasev2.HeartRateInBeatsPerMinuteT;
import net.kenevans.trainingcenterdatabasev2.PlanT;
import net.kenevans.trainingcenterdatabasev2.PositionT;
import net.kenevans.trainingcenterdatabasev2.SensorStateT;
import net.kenevans.trainingcenterdatabasev2.TrackT;
import net.kenevans.trainingcenterdatabasev2.TrackpointT;
import net.kenevans.trainingcenterdatabasev2.TrainingCenterDatabaseT;
import net.kenevans.trainingcenterdatabasev2.TrainingT;
import net.kenevans.trainingcenterdatabasev2.TrainingTypeT;
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
		GPXParser.setMetaDataTime(gpx);

		ActivityListT activityList;
		List<ActivityT> activities;
		List<ActivityLapT> laps;
		List<TrackT> tracks;
		List<TrackpointT> trackPoints;
		// ExtensionsT trackPointExt;
		Short cad, hr;
		Double ele, dist;
		double lat, lon;
		HeartRateInBeatsPerMinuteT hrBpm;
		PositionT position;
		SensorStateT sensorState;
		XMLGregorianCalendar time, id;
		AbstractSourceT creator;
		String creatorName, trainingPlanName, trainingPlanType;
		TrainingT training;
		PlanT plan;
		TrainingTypeT trainingType;

		boolean error = false;
		TrkType trkType = null;
		TrksegType trksegType = null;
		WptType wptType = null;

		activityList = tcx.getActivities();
		activities = activityList.getActivity();
		for (ActivityT activity : activities) {
			id = activity.getId();
			creator = activity.getCreator();
			creatorName = creator.getName();
			
			training = activity.getTraining();
			trainingType = null;
			trainingPlanName=null;
			trainingPlanType = null;
			if (training != null) {
				plan = training.getPlan();
				if (plan != null) {
					trainingType = plan.getType();
					trainingPlanName = plan.getName();
					if(trainingType != null) {
						trainingPlanType = trainingType.value();
					}
				}
			}
			System.out.println("Activity : " + id + " creator=" + creatorName);
			System.out.println("  Plan: " + trainingPlanName + " Type: " + trainingPlanType);
			laps = activity.getLap();
			for (ActivityLapT lap : laps) {
				tracks = lap.getTrack();
				int nTracks = 0;
				for (TrackT track : tracks) {
					nTracks++;
					trkType = new TrkType();
					if (trkType == null) {
						SWTUtils.errMsg("Track " + nTracks + ": Cannot create track:\n" + file.getName());
						error = true;
						break;
					}
					gpx.getTrk().add(trkType);
					trksegType = new TrksegType();
					if (trksegType == null) {
						SWTUtils.errMsg("Track " + nTracks + ": Cannot create track segment:\n" + file.getName());
						error = true;
						break;
					}
					trkType.getTrkseg().add(trksegType);
					trackPoints = track.getTrackpoint();
					int nTrackPoints = 0;
					for (TrackpointT trackPoint : trackPoints) {
						nTrackPoints++;
						wptType = new WptType();
						if (wptType == null) {
							SWTUtils.errMsg("Track " + nTracks + "TrackPoint " + nTrackPoints + ": Cannot trackpoint:\n"
									+ file.getName());
							error = true;
							break;
						}
						position = trackPoint.getPosition();
						if (position != null) {
							lat = position.getLatitudeDegrees();
							lon = position.getLongitudeDegrees();
							wptType.setLat(new BigDecimal(lat));
							wptType.setLon(new BigDecimal(lon));
							// Only add the segment if there is a valid lat and lon
							trksegType.getTrkpt().add(wptType);
						} else {
							continue;
						}
						ele = trackPoint.getAltitudeMeters();
						cad = trackPoint.getCadence();
						dist = trackPoint.getDistanceMeters();
						hrBpm = trackPoint.getHeartRateBpm();
						if (hrBpm != null) {
							hr = hrBpm.getValue();
						} else {
							hr = null;
						}
						if (ele != null) {
							wptType.setEle(new BigDecimal(ele));
						}
						time = trackPoint.getTime();
						if (time != null) {
							wptType.setTime(time);
						}
					}
				}
			}
		}
		if (error)
			return null;
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
