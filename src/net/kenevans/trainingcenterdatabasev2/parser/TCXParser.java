package net.kenevans.trainingcenterdatabasev2.parser;

import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import net.kenevans.gpxtrackpointextensionv2.ExtensionsType;
import net.kenevans.gpxtrackpointextensionv2.GpxType;
import net.kenevans.gpxtrackpointextensionv2.MetadataType;
import net.kenevans.gpxtrackpointextensionv2.PersonType;
import net.kenevans.gpxtrackpointextensionv2.TrackPointExtensionT;
import net.kenevans.gpxtrackpointextensionv2.TrkType;
import net.kenevans.gpxtrackpointextensionv2.TrksegType;
import net.kenevans.gpxtrackpointextensionv2.WptType;
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

/*
 * Created on Jan 22, 2019
 * By Kenneth Evans, Jr.
 */

public class TCXParser
{
    private static String AUTHOR = "TCXParser (kenevans.net)";
    /** Hard-coded file name for testing with the main method. */
    // private static final String TEST_FILE =
    // "C:/Users/evans/Documents/GPSLink/Polar/Kenneth_Evans_2018-08-10_09-02-44.tcx";
    private static final String TEST_FILE = "C:/Users/evans/Documents/GPSLink/Polar/Kenneth_Evans_2019-01-21_14-50-53.tcx";
    // private static final String TEST_FILE =
    // "C:/Users/evans/Documents/GPSLink/FitnessHistoryDetail.tcx";
    private static boolean PARSE_OUTPUT = false;
    private static boolean MARSHALL_OUTPUT = true;

    /** This is the package specified when XJC was run. */
    private static String TRAINING_CENTER_DATABASE_V2_PACKAGE = "net.kenevans.trainingcenterdatabasev2";

    /**
     * Save a TrainingCenterDatabaseT object into a file with the given name.
     * 
     * @param tcx
     * @param fileName
     * @throws JAXBException
     */
    public static void save(String creator, TrainingCenterDatabaseT tcx,
        String fileName) throws JAXBException {
        save(creator, tcx, new File(fileName));
    }

    /**
     * Save a TrainingCenterDatabaseT object into a File.
     * 
     * @param tcx
     * @param file
     * @throws JAXBException
     */
    public static void save(String creator, TrainingCenterDatabaseT tcx,
        File file) throws JAXBException {
        // Create a new JAXBElement<TrainingCenterDatabaseT> for the marshaller
        QName qName = new QName(
            "http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2",
            "tcx");
        JAXBElement<TrainingCenterDatabaseT> root = new JAXBElement<TrainingCenterDatabaseT>(
            qName, TrainingCenterDatabaseT.class, tcx);
        // Create a context
        JAXBContext jc = JAXBContext
            .newInstance(TRAINING_CENTER_DATABASE_V2_PACKAGE);
        // Create a marshaller
        Marshaller marshaller = jc.createMarshaller();
        // Set it to be formatted, otherwise it is one long line
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // Need to set the schema location to pass Xerces 3.1.1 SaxCount
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
            "http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2"
                + " http://www.garmin.com/xmlschemas/TrainingCenterDatabasev2.xsd");
        // Marshal
        marshaller.marshal(root, file);
    }

    /**
     * Print a TrainingCenterDatabaseT object into an OutputStream. Should do
     * the same as save() except the last line writes to the OutputStream
     * instead of a File.
     * 
     * @param gpx
     * @param out
     * @throws JAXBException
     */
    public static void print(String creator, TrainingCenterDatabaseT tcx,
        OutputStream out) throws JAXBException {
        // The code here should be the same as in save except for the last line.

        // Create a new JAXBElement<TrainingCenterDatabaseT> for the marshaller
        QName qName = new QName(
            "http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2",
            "tcx");
        JAXBElement<TrainingCenterDatabaseT> root = new JAXBElement<TrainingCenterDatabaseT>(
            qName, TrainingCenterDatabaseT.class, tcx);
        // Create a context
        JAXBContext jc = JAXBContext
            .newInstance(TRAINING_CENTER_DATABASE_V2_PACKAGE);
        // Create a marshaller
        Marshaller marshaller = jc.createMarshaller();
        // Set it to be formatted, otherwise it is one long line
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // Need to set the schema location to pass Xerces 3.1.1 SaxCount
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
            "http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2"
                + " http://www.garmin.com/xmlschemas/TrainingCenterDatabasev2.xsd");
        // Marshal
        marshaller.marshal(root, out);
    }

    /**
     * Parses a GPX file with the given name.
     * 
     * @param fileName The file name to parse.
     * @return The GpxType corresponding to the top level of the input file.
     * @throws JAXBException
     */
    public static TrainingCenterDatabaseT parse(String fileName)
        throws JAXBException {
        return parse(new File(fileName));
    }

    /**
     * Parses a TCX file.
     * 
     * @param file The File to parse.
     * @return The TrainingCenterDatabaseT corresponding to the top level of the
     *         input file.
     * @throws JAXBException
     */
    @SuppressWarnings("unchecked")
    public static TrainingCenterDatabaseT parse(File file)
        throws JAXBException {
        TrainingCenterDatabaseT tcx = null;
        JAXBContext jc = JAXBContext
            .newInstance(TRAINING_CENTER_DATABASE_V2_PACKAGE);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        try {
            JAXBElement<TrainingCenterDatabaseT> root = (JAXBElement<TrainingCenterDatabaseT>)unmarshaller
                .unmarshal(file);
            tcx = root.getValue();
        } catch(JAXBException ex) {
            // Some other problem, rethrow the exception
            throw (ex);
        }
        return tcx;
    }

    /**
     * Converts a TCX TrainingCenterDatabaseT to a GPX 1.1 GpxType type by
     * copying common fields. Note that this implementation may not be complete
     * and may not convert everything that could be converted.
     * 
     * @param tcx The TrainingCenterDatabaseT to convert.
     * @return The GpxType.
     */
    public static GpxType convertTCXtoGpx(TrainingCenterDatabaseT tcx) {
        if(tcx == null) {
            return null;
        }
        String desc, notes, planName;
        double lat, lon;
        Double ele;
        // TCX types
        ActivityListT activities;
        List<ActivityT> activityList;
        List<ActivityLapT> lapList;
        List<TrackT> trackList;
        List<TrackpointT> trackpointList;
        PositionT position;
        HeartRateInBeatsPerMinuteT hrBpm;
        Short hr, cad;
        XMLGregorianCalendar time;
        TrainingT training;
        PlanT plan;
        TrainingTypeT trainingType;
        // GPX types
        GpxType gpxNew;
        MetadataType metadata;
        PersonType person;
        TrkType trk;
        TrksegType trkseg;
        WptType trkpt;
        ExtensionsType extension;
        TrackPointExtensionT trkptExension;
        gpxNew = new GpxType();
        // Metadata
        metadata = new MetadataType();
        person = new PersonType();
        person.setName(AUTHOR);
        metadata.setAuthor(person);
        desc = getMetadataDescriptionFromTcx(tcx);
        if(desc != null) {
            metadata.setDesc(desc);
        }
        gpxNew.setMetadata(metadata);

        // Check if some trackpoints have position and some not.
        // Do not want to make a trackpoint for the ones with no position
        // to avoid lat,lon = 0,0 points
        boolean positionFound = positionFound(tcx);

        // Activities (Correspond to a track)
        activities = tcx.getActivities();
        // Loop over activities
        activityList = activities.getActivity();
        for(ActivityT activity : activityList) {
            trk = new TrkType();
            gpxNew.getTrk().add(trk);
            // Get the description from the notes and training plan
            desc = "";
            planName = "";
            notes = "";
            training = activity.getTraining();
            if(training != null) {
                plan = training.getPlan();
                if(plan != null) {
                    trainingType = plan.getType();
                    desc += "Training Type: " + trainingType;
                    planName = plan.getName();
                    if(planName != null && !planName.isEmpty()) {
                        if(!desc.isEmpty()) {
                            desc += " ";
                        }
                        desc += "PlanName: " + planName;
                    }
                }
            }
            notes = activity.getNotes();
            if(notes != null && !notes.isEmpty()) {
                if(!desc.isEmpty()) {
                    desc += " ";
                }
                desc += "Notes: " + notes;
            }
            if(!desc.isEmpty()) {
                trk.setDesc(desc);
            }
            // Loop over laps (Correspond to a track segment(s))
            lapList = activity.getLap();
            for(ActivityLapT lap : lapList) {
                // Loop over tracks
                trackList = lap.getTrack();
                for(TrackT track : trackList) {
                    trackpointList = track.getTrackpoint();
                    trkseg = new TrksegType();
                    trk.getTrkseg().add(trkseg);
                    // loop over trackpoints
                    for(TrackpointT trackPoint : trackpointList) {
                        trkpt = new WptType();
                        position = trackPoint.getPosition();
                        if(position != null) {
                            lat = position.getLatitudeDegrees();
                            lon = position.getLongitudeDegrees();
                            trkpt.setLat(BigDecimal.valueOf(lat));
                            trkpt.setLon(BigDecimal.valueOf(lon));
                            trkseg.getTrkpt().add(trkpt);
                        } else if(!positionFound) {
                            trkpt.setLat(BigDecimal.valueOf(0));
                            trkpt.setLon(BigDecimal.valueOf(0));
                            trkseg.getTrkpt().add(trkpt);
                        } else {
                            // This TCX has positions. This trackpoint doesn't
                            // and we don't want to write lat,lon = 0,0 with
                            // valid positions, so skip this trackpoint.
                            continue;
                        }
                        ele = trackPoint.getAltitudeMeters();
                        if(ele == null) {
                            trkpt.setEle(BigDecimal.valueOf(0));
                        } else {
                            trkpt.setEle(BigDecimal.valueOf(ele));
                        }
                        time = trackPoint.getTime();
                        if(time != null) {
                            trkpt.setTime(time);
                        }
                        // Extension
                        hrBpm = trackPoint.getHeartRateBpm();
                        cad = trackPoint.getCadence();
                        if(hrBpm != null || cad != null) {
                            extension = new ExtensionsType();
                            trkptExension = new TrackPointExtensionT();
                            if(hrBpm != null) {
                                hr = hrBpm.getValue();
                                if(hr != null) {
                                    trkptExension.setHr(hr);
                                }
                            }
                            if(cad != null) {
                                trkptExension.setCad(cad);
                            }
                            extension.getAny().add(trkptExension);
                            trkpt.setExtensions(extension);
                        }
                    }
                }
            }
        }
        return gpxNew;
    }

    public static boolean positionFound(TrainingCenterDatabaseT tcx) {
        ActivityListT activities;
        List<ActivityT> activityList;
        List<ActivityLapT> lapList;
        List<TrackT> trackList;
        List<TrackpointT> trackpointList;
        PositionT position;
        activities = tcx.getActivities();

        // Loop over activities
        activityList = activities.getActivity();
        for(ActivityT activity : activityList) {
            // Loop over laps (Correspond to a track segment(s))
            lapList = activity.getLap();
            for(ActivityLapT lap : lapList) {
                // Loop over tracks
                trackList = lap.getTrack();
                for(TrackT track : trackList) {
                    trackpointList = track.getTrackpoint();
                    // loop over trackpoints
                    for(TrackpointT trackPoint : trackpointList) {
                        position = trackPoint.getPosition();
                        if(position != null) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static String getMetadataDescriptionFromTcx(
        TrainingCenterDatabaseT tcx) {
        if(tcx == null) {
            return null;
        }
        AbstractSourceT author = tcx.getAuthor();
        if(author == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if(author.getName() != null) {
            sb.append(author.getName());
        }
        String desc = sb.toString();
        if(desc.length() == 0) {
            return null;
        }
        return desc;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        String fileName = TEST_FILE;
        System.out.println(fileName);
        TrainingCenterDatabaseT tcx = null;
        try {
            tcx = parse(fileName);
        } catch(JAXBException ex) {
            System.out
                .println("Error creating JAXBContext: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }

        if(PARSE_OUTPUT) {
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

            activityList = tcx.getActivities();
            activities = activityList.getActivity();
            for(ActivityT activity : activities) {
                id = activity.getId();
                creator = activity.getCreator();
                creatorName = creator.getName();
                training = activity.getTraining();
                trainingType = null;
                trainingPlanName = null;
                trainingPlanType = null;
                if(training != null) {
                    plan = training.getPlan();
                    if(plan != null) {
                        trainingType = plan.getType();
                        trainingPlanName = plan.getName();
                        if(trainingType != null) {
                            trainingPlanType = trainingType.value();
                        }
                    }
                }
                System.out
                    .println("Activity : " + id + " creator=" + creatorName);
                System.out.println("  Plan: " + trainingPlanName + " Type: "
                    + trainingPlanType);
                laps = activity.getLap();
                for(ActivityLapT lap : laps) {
                    tracks = lap.getTrack();
                    int nTracks = 0;
                    for(TrackT track : tracks) {
                        System.out.println("  Track " + nTracks++);
                        trackPoints = track.getTrackpoint();
                        for(TrackpointT trackPoint : trackPoints) {
                            ele = trackPoint.getAltitudeMeters();
                            cad = trackPoint.getCadence();
                            dist = trackPoint.getDistanceMeters();
                            hrBpm = trackPoint.getHeartRateBpm();
                            if(hrBpm != null) {
                                hr = hrBpm.getValue();
                            } else {
                                hr = null;
                            }
                            position = trackPoint.getPosition();
                            if(position == null) {
                                lat = Double.NaN;
                                lon = Double.NaN;
                            } else {
                                lat = position.getLatitudeDegrees();
                                lon = position.getLongitudeDegrees();
                            }
                            // trackPointExt = trackPoint.getExtensions();
                            sensorState = trackPoint.getSensorState();
                            time = trackPoint.getTime();
                            System.out.println("    Trackpoint " + time);
                            System.out.println("      lat=" + lat + " lon="
                                + lon + " ele=" + ele + " hr=" + hr + " cad="
                                + cad + " dist=" + dist + " sensorState="
                                + sensorState);
                        }
                    }
                }
            }
        }

        if(MARSHALL_OUTPUT) {
            System.out.println();
            try {
                print("TCXParser", tcx, System.out);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
