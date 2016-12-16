/**
 * FILE: PolygonJoinTest.java
 * PATH: org.datasyslab.geospark.spatialOperator.PolygonJoinTest.java
 * Copyright (c) 2017 Arizona State University Data Systems Lab.
 * All rights reserved.
 */
package org.datasyslab.geospark.spatialOperator;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.datasyslab.geospark.enums.FileDataSplitter;
import org.datasyslab.geospark.enums.GridType;
import org.datasyslab.geospark.enums.IndexType;
import org.datasyslab.geospark.spatialRDD.PolygonRDD;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Arizona State University DataSystems Lab
 *
 */

import com.vividsolutions.jts.geom.Polygon;

import scala.Tuple2;


// TODO: Auto-generated Javadoc
/**
 * The Class PolygonJoinTest.
 */
public class PolygonJoinTest {
    
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
        SparkConf conf = new SparkConf().setAppName("PolygonJoin").setMaster("local[2]");
        sc = new JavaSparkContext(conf);
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        prop = new Properties();
        input = PolygonJoinTest.class.getClassLoader().getResourceAsStream("polygon.test.properties");
        InputLocation = "file://"+PolygonJoinTest.class.getClassLoader().getResource("primaryroads-polygon.csv").getPath();
        offset = 0;
        splitter = null;
        gridType = null;
        indexType = null;
        numPartitions = 0;

        try {
            // load a properties file
            prop.load(input);

            //InputLocation = prop.getProperty("inputLocation");
            InputLocation = "file://"+PolygonJoinTest.class.getClassLoader().getResource(prop.getProperty("inputLocation")).getPath();
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
     * Tear down.
     */
    @AfterClass
    public static void TearDown() {
        sc.stop();
    }

    /**
     * Test spatial join query.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSpatialJoinQuery() throws Exception {

    	PolygonRDD polygonRDD = new PolygonRDD(sc, InputLocation, offset, splitter, numPartitions);

        PolygonRDD objectRDD = new PolygonRDD(sc, InputLocation, offset, splitter, gridType, numPartitions);

        JoinQuery joinQuery = new JoinQuery(sc,objectRDD,polygonRDD); 
        
        List<Tuple2<Polygon, HashSet<Polygon>>> result = joinQuery.SpatialJoinQuery(objectRDD,polygonRDD).collect();
        assert result.get(0)._1().getUserData()!=null;
        for(int i=0;i<result.size();i++)
        {
        	if(result.get(i)._2().size()!=0)
        	{
        		assert result.get(i)._2().iterator().next().getUserData()!=null;
        	}
        }
    }

    /**
     * Test spatial join query using index exception.
     *
     * @throws Exception the exception
     */
    @Test(expected = NullPointerException.class)
    public void testSpatialJoinQueryUsingIndexException() throws Exception {
    	PolygonRDD polygonRDD = new PolygonRDD(sc, InputLocation, offset, splitter, numPartitions);

    	PolygonRDD objectRDD = new PolygonRDD(sc, InputLocation, offset, splitter, numPartitions);
        
        JoinQuery joinQuery = new JoinQuery(sc,objectRDD,polygonRDD);
        
        //This should throw exception since the previous constructor doesn't build a grided RDD.
        List<Tuple2<Polygon, HashSet<Polygon>>> result = joinQuery.SpatialJoinQueryUsingIndex(objectRDD,polygonRDD).collect();

    }

    /**
     * Test spatial join query using index.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSpatialJoinQueryUsingIndex() throws Exception {

    	PolygonRDD polygonRDD = new PolygonRDD(sc, InputLocation, offset, splitter, numPartitions);

    	PolygonRDD objectRDD = new PolygonRDD(sc, InputLocation, offset, splitter, gridType, numPartitions);

    	objectRDD.buildIndex(IndexType.RTREE);

        JoinQuery joinQuery = new JoinQuery(sc,objectRDD,polygonRDD);
        
        List<Tuple2<Polygon, HashSet<Polygon>>> result = joinQuery.SpatialJoinQueryUsingIndex(objectRDD,polygonRDD).collect();
        assert result.get(0)._1().getUserData()!=null;
        for(int i=0;i<result.size();i++)
        {
        	if(result.get(i)._2().size()!=0)
        	{
        		assert result.get(i)._2().iterator().next().getUserData()!=null;
        	}
        }
    }

    /**
     * Test join correctness.
     *
     * @throws Exception the exception
     */
    @Test
    public void testJoinCorrectness() throws Exception {

        PolygonRDD queryWindowRDD1 = new PolygonRDD(sc, InputLocation, offset, splitter);

        PolygonRDD objectRDD1 = new PolygonRDD(sc, InputLocation, offset, splitter, gridType, 20);

        JoinQuery joinQuery1 = new JoinQuery(sc,objectRDD1,queryWindowRDD1); 
        
        List<Tuple2<Polygon, HashSet<Polygon>>> result1 = joinQuery1.SpatialJoinQuery(objectRDD1,queryWindowRDD1).collect();
        
        
        PolygonRDD queryWindowRDD2 = new PolygonRDD(sc, InputLocation, offset, splitter);
        
        PolygonRDD objectRDD2 = new PolygonRDD(sc, InputLocation, offset, splitter, gridType, 30);

        JoinQuery joinQuery2 = new JoinQuery(sc,objectRDD2,queryWindowRDD2); 
        
        List<Tuple2<Polygon, HashSet<Polygon>>> result2 = joinQuery2.SpatialJoinQuery(objectRDD2,queryWindowRDD2).collect();
        
        
        PolygonRDD queryWindowRDD3 = new PolygonRDD(sc, InputLocation, offset, splitter);
        
        PolygonRDD objectRDD3 = new PolygonRDD(sc, InputLocation, offset, splitter, gridType, 40);

        JoinQuery joinQuery3 = new JoinQuery(sc,objectRDD3,queryWindowRDD3); 
        
        List<Tuple2<Polygon, HashSet<Polygon>>> result3 = joinQuery3.SpatialJoinQuery(objectRDD3,queryWindowRDD3).collect();
        
        
        PolygonRDD queryWindowRDD4 = new PolygonRDD(sc, InputLocation, offset, splitter);

        PolygonRDD objectRDD4 = new PolygonRDD(sc, InputLocation, offset, splitter, GridType.EQUALGRID, 20);

        JoinQuery joinQuery4 = new JoinQuery(sc,objectRDD4,queryWindowRDD4); 
        
        List<Tuple2<Polygon, HashSet<Polygon>>> result4 = joinQuery4.SpatialJoinQuery(objectRDD4,queryWindowRDD4).collect();
        
        
        PolygonRDD queryWindowRDD5 = new PolygonRDD(sc, InputLocation, offset, splitter);
        
        PolygonRDD objectRDD5 = new PolygonRDD(sc, InputLocation, offset, splitter, GridType.RTREE, 20);

        JoinQuery joinQuery5 = new JoinQuery(sc,objectRDD5,queryWindowRDD5); 
        
        List<Tuple2<Polygon, HashSet<Polygon>>> result5 = joinQuery5.SpatialJoinQuery(objectRDD5,queryWindowRDD5).collect();
        
        
        PolygonRDD queryWindowRDD6 = new PolygonRDD(sc, InputLocation, offset, splitter);
        
        PolygonRDD objectRDD6 = new PolygonRDD(sc, InputLocation, offset, splitter, GridType.VORONOI, 20);

        JoinQuery joinQuery6 = new JoinQuery(sc,objectRDD6,queryWindowRDD6); 
        
        List<Tuple2<Polygon, HashSet<Polygon>>> result6 = joinQuery6.SpatialJoinQuery(objectRDD6,queryWindowRDD6).collect();
        
        
        PolygonRDD queryWindowRDD7 = new PolygonRDD(sc, InputLocation, offset, splitter);

        PolygonRDD objectRDD7 = new PolygonRDD(sc, InputLocation, offset, splitter, GridType.EQUALGRID, 20);
        
        objectRDD7.buildIndex(IndexType.RTREE);

        JoinQuery joinQuery7 = new JoinQuery(sc,objectRDD7,queryWindowRDD7); 
        
        List<Tuple2<Polygon, HashSet<Polygon>>> result7 = joinQuery7.SpatialJoinQueryUsingIndex(objectRDD7,queryWindowRDD7).collect();
        
        
        PolygonRDD queryWindowRDD8 = new PolygonRDD(sc, InputLocation, offset, splitter);
        
        PolygonRDD objectRDD8 = new PolygonRDD(sc, InputLocation, offset, splitter, GridType.RTREE, 40);

        objectRDD8.buildIndex(IndexType.RTREE);
        
        JoinQuery joinQuery8 = new JoinQuery(sc,objectRDD8,queryWindowRDD8); 
        
        List<Tuple2<Polygon, HashSet<Polygon>>> result8 = joinQuery8.SpatialJoinQueryUsingIndex(objectRDD8,queryWindowRDD8).collect();
        
        
        PolygonRDD queryWindowRDD9 = new PolygonRDD(sc, InputLocation, offset, splitter);
        
        PolygonRDD objectRDD9 = new PolygonRDD(sc, InputLocation, offset, splitter, GridType.VORONOI, 20);

        objectRDD9.buildIndex(IndexType.RTREE);
        
        JoinQuery joinQuery9 = new JoinQuery(sc,objectRDD9,queryWindowRDD9); 
        
        List<Tuple2<Polygon, HashSet<Polygon>>> result9 = joinQuery9.SpatialJoinQueryUsingIndex(objectRDD9,queryWindowRDD9).collect();
        if (result1.size()!=result2.size() || result1.size()!=result3.size()
        		|| result1.size()!=result4.size()|| result1.size()!=result5.size()
        		|| result1.size()!=result6.size()|| result1.size()!=result7.size()
        		|| result1.size()!=result8.size()|| result1.size()!=result9.size()
        		)
        {
        	System.out.println("-----Polygon join results are not consistent-----");
        	System.out.println(result1.size());
        	System.out.println(result2.size());
        	System.out.println(result3.size());
        	System.out.println(result4.size());
        	System.out.println(result5.size());
        	System.out.println(result6.size());
        	System.out.println(result7.size());
        	System.out.println(result8.size());
        	System.out.println(result9.size());
        	System.out.println("-----Polygon join results are not consistent--Done---");
        	throw new Exception("Polygon join results are not consistent!");
        }
        
        
    }


}