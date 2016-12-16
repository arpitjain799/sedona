/**
 * FILE: RectangleRDDTest.java
 * PATH: org.datasyslab.geospark.spatialRDD.RectangleRDDTest.java
 * Copyright (c) 2017 Arizona State University Data Systems Lab.
 * All rights reserved.
 */
package org.datasyslab.geospark.spatialRDD;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.datasyslab.geospark.enums.FileDataSplitter;
import org.datasyslab.geospark.enums.GridType;
import org.datasyslab.geospark.enums.IndexType;
import org.datasyslab.geospark.geometryObjects.EnvelopeWithGrid;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vividsolutions.jts.geom.Polygon;


// TODO: Auto-generated Javadoc
/**
 * The Class RectangleRDDTest.
 */
public class RectangleRDDTest implements Serializable{
    
    /** The sc. */
    public static JavaSparkContext sc;
    
    /** The prop. */
    static Properties prop;
    
    /** The input. */
    static InputStream input;
    
    /** The Input location. */
    static String InputLocation;
    
    /** The offset. */
    static Integer offset;
    
    /** The splitter. */
    static FileDataSplitter splitter;
    
    /** The grid type. */
    static GridType gridType;
    
    /** The index type. */
    static IndexType indexType;
    
    /** The num partitions. */
    static Integer numPartitions;
    
    /**
     * Once executed before all.
     */
    @BeforeClass
    public static void onceExecutedBeforeAll() {
        SparkConf conf = new SparkConf().setAppName("RectangleTest").setMaster("local[2]");
        sc = new JavaSparkContext(conf);
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        prop = new Properties();
        input = RectangleRDDTest.class.getClassLoader().getResourceAsStream("rectangle.test.properties");
        InputLocation = "file://"+RectangleRDDTest.class.getClassLoader().getResource("primaryroads.csv").getPath();
        offset = 0;
        splitter = null;
        gridType = null;
        indexType = null;
        numPartitions = 0;

        try {
            // load a properties file
            prop.load(input);
            //InputLocation = prop.getProperty("inputLocation");
            InputLocation = "file://"+RectangleRDDTest.class.getClassLoader().getResource(prop.getProperty("inputLocation")).getPath();
            offset = Integer.parseInt(prop.getProperty("offset"));
            splitter = FileDataSplitter.getFileDataSplitter(prop.getProperty("splitter"));
            gridType = GridType.getGridType(prop.getProperty("gridType"));
            indexType = IndexType.getIndexType(prop.getProperty("indexType"));
            numPartitions = Integer.parseInt(prop.getProperty("numPartitions"));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    /**
     * Test constructor.
     *
     * @throws Exception the exception
     */
    /*
        This test case will load a sample data file and
     */
    @Test
    public void testConstructor() throws Exception {
        //The grid type is X right now.
        RectangleRDD rectangleRDD = new RectangleRDD(sc, InputLocation, offset, splitter, gridType, numPartitions);
        //todo: Set this to debug level


        //todo: Move this into log4j.
        Map<Integer, Long> map = rectangleRDD.gridRectangleRDD.countByKey();
        for (Entry<Integer, Long> entry : map.entrySet()) {
            Long number = (Long) entry.getValue();
            Double percentage = number.doubleValue() / rectangleRDD.totalNumberOfRecords;
            //System.out.println(entry.getKey() + " : " + String.format("%.4f", percentage));
        }
    }

    /*
     *  This test case test whether the X-Y grid can be build correctly.
     */
    /*
    @Test
    public void testXYGrid() throws Exception {
        RectangleRDD rectangleRDD = new RectangleRDD(sc, InputLocation, offset, splitter, "X-Y", 10);
        for (EnvelopeWithGrid d : rectangleRDD.grids) {
        	System.out.println("RectangleRDD grids: "+d.grid);
        }
        //todo: Move this into log4j.
        Map<Integer, Object> map = rectangleRDD.gridRectangleRDD.countByKey();

        System.out.println(map.size());

        for (Map.Entry<Integer, Object> entry : map.entrySet()) {
            Long number = (Long) entry.getValue();
            Double percentage = number.doubleValue() / rectangleRDD.totalNumberOfRecords;
            System.out.println(entry.getKey() + " : " + String.format("%.4f", percentage));
        }
    }*/
    /**
     * Test equal size grids spatial partitioing.
     *
     * @throws Exception the exception
     */
    /*
     *  This test case test whether the equal size grids can be build correctly.
     */
    @Test
    public void testEqualSizeGridsSpatialPartitioing() throws Exception {
    	RectangleRDD rectangleRDD = new RectangleRDD(sc, InputLocation, offset, splitter, GridType.EQUALGRID, 10);
        /*for (EnvelopeWithGrid d : rectangleRDD.grids) {
        	System.out.println("RectangleRDD spatial partitioning grids: "+d.grid);
        }*/
        //todo: Move this into log4j.
        Map<Integer, Long> map = rectangleRDD.gridRectangleRDD.countByKey();

        //System.out.println(map.size());

        for (Entry<Integer, Long> entry : map.entrySet()) {
            Long number = (Long) entry.getValue();
            Double percentage = number.doubleValue() / rectangleRDD.totalNumberOfRecords;
           // System.out.println(entry.getKey() + " : " + String.format("%.4f", percentage));
        }
    }
    
    /**
     * Test hilbert curve spatial partitioing.
     *
     * @throws Exception the exception
     */
    /*
     *  This test case test whether the Hilbert Curve grid can be build correctly.
     */
    @Test
    public void testHilbertCurveSpatialPartitioing() throws Exception {
    	RectangleRDD rectangleRDD = new RectangleRDD(sc, InputLocation, offset, splitter, GridType.HILBERT, 10);
        /*for (EnvelopeWithGrid d : rectangleRDD.grids) {
        	System.out.println("RectangleRDD spatial partitioning grids: "+d.grid);
        }*/
        //todo: Move this into log4j.
        Map<Integer, Long> map = rectangleRDD.gridRectangleRDD.countByKey();

        //System.out.println(map.size());

        for (Entry<Integer, Long> entry : map.entrySet()) {
            Long number = (Long) entry.getValue();
            Double percentage = number.doubleValue() / rectangleRDD.totalNumberOfRecords;
           // System.out.println(entry.getKey() + " : " + String.format("%.4f", percentage));
        }
    }
    
    /**
     * Test R tree spatial partitioing.
     *
     * @throws Exception the exception
     */
    /*
     *  This test case test whether the STR-Tree grid can be build correctly.
     */
    @Test
    public void testRTreeSpatialPartitioing() throws Exception {
    	RectangleRDD rectangleRDD = new RectangleRDD(sc, InputLocation, offset, splitter, GridType.RTREE, 10);
        for (EnvelopeWithGrid d : rectangleRDD.grids) {
        	System.out.println("RectangleRDD spatial partitioning grids: "+d.grid);
        }
        //todo: Move this into log4j.
        Map<Integer, Long> map = rectangleRDD.gridRectangleRDD.countByKey();

       // System.out.println(map.size());

        for (Entry<Integer, Long> entry : map.entrySet()) {
            Long number = (Long) entry.getValue();
            Double percentage = number.doubleValue() / rectangleRDD.totalNumberOfRecords;
            //System.out.println(entry.getKey() + " : " + String.format("%.4f", percentage));
        }
    }
    
    /**
     * Test voronoi spatial partitioing.
     *
     * @throws Exception the exception
     */
    /*
     *  This test case test whether the Voronoi grid can be build correctly.
     */
    @Test
    public void testVoronoiSpatialPartitioing() throws Exception {
    	RectangleRDD rectangleRDD = new RectangleRDD(sc, InputLocation, offset, splitter, GridType.VORONOI, 10);
        /*for (EnvelopeWithGrid d : rectangleRDD.grids) {
        	System.out.println("RectangleRDD spatial partitioning grids: "+d.grid);
        }*/
        //todo: Move this into log4j.
        Map<Integer, Long> map = rectangleRDD.gridRectangleRDD.countByKey();

        //System.out.println(map.size());

        for (Entry<Integer, Long> entry : map.entrySet()) {
            Long number = (Long) entry.getValue();
            Double percentage = number.doubleValue() / rectangleRDD.totalNumberOfRecords;
            //System.out.println(entry.getKey() + " : " + String.format("%.4f", percentage));
        }
    }

    
    /**
     * Test build index without set grid.
     *
     * @throws Exception the exception
     */
    /*
     * If we try to build a index on a rawRectangleRDD which is not construct with grid. We shall see an error.
     */
    @Test//(expected=IllegalClassException.class)
    public void testBuildIndexWithoutSetGrid() throws Exception {
        RectangleRDD rectangleRDD = new RectangleRDD(sc, InputLocation, offset, splitter, numPartitions);
        rectangleRDD.buildIndex(IndexType.RTREE);
    }

    /**
     * Test build index.
     *
     * @throws Exception the exception
     */
    /*
        Test build Index.
     */
    @Test
    public void testBuildIndex() throws Exception {
        RectangleRDD rectangleRDD = new RectangleRDD(sc, InputLocation, offset, splitter, gridType, numPartitions);
        rectangleRDD.buildIndex(IndexType.RTREE);
        List<Polygon> result = rectangleRDD.indexedRDD.take(1).get(0)._2().query(rectangleRDD.boundaryEnvelope);
        //todo, here have their might be a problem where the result is essentially a point(dirty data) and jts will throw exception.
        try {
            for(Polygon e: result) {
                //System.out.println(e.getEnvelopeInternal());
            }
        } catch (Exception e) {

        }
    }
    
    /**
     * Test build with no exists grid.
     *
     * @throws Exception the exception
     */
    /*
     *  If we want to use a grid type that is not supported yet, an exception will be throwed out.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testBuildWithNoExistsGrid() throws Exception {
        RectangleRDD rectangleRDD = new RectangleRDD(sc, InputLocation, offset, splitter, null, numPartitions);
    }

    /**
     * Test too large partition number.
     *
     * @throws Exception the exception
     */
    /*
     * If the partition number is set too large, we will
     */
    @Test(expected=IllegalArgumentException.class)
    public void testTooLargePartitionNumber() throws Exception {
        RectangleRDD rectangleRDD = new RectangleRDD(sc, InputLocation, offset, splitter, gridType, 1000000);
    }

    /**
     * Tear down.
     */
    @AfterClass
    public static void TearDown() {
        sc.stop();
    }
}