/**
 * FILE: HilbertPartitioning.java
 * PATH: org.datasyslab.geospark.spatialPartitioning.HilbertPartitioning.java
 * Copyright (c) 2017 Arizona State University Data Systems Lab.
 * All rights reserved.
 */
package org.datasyslab.geospark.spatialPartitioning;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;

import org.datasyslab.geospark.geometryObjects.EnvelopeWithGrid;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

// TODO: Auto-generated Javadoc
/**
 * The Class HilbertPartitioning.
 */
public class HilbertPartitioning implements Serializable{

	/** The splits. */
	//Partition ID
	protected int[] splits;
	
	/** The grids. */
	//Partition boundaries
	HashSet<EnvelopeWithGrid> grids;
	
	/**
	 * Instantiates a new hilbert partitioning.
	 *
	 * @param SampleList the sample list
	 * @param boundary the boundary
	 * @param partitions the partitions
	 */
	public HilbertPartitioning(Point[] SampleList,Envelope boundary,int partitions)
	{
		//this.boundary=boundary;
		int gridResolution=Short.MAX_VALUE;
	    int[] hValues = new int[SampleList.length];
	    Envelope [] gridWithoutID=new Envelope[partitions];
	    HashSet<EnvelopeWithGrid> gridWithID= new HashSet<EnvelopeWithGrid>();
	    for (int i = 0; i < SampleList.length; i++){
	    	int x=locationMapping(boundary.getMinX(),boundary.getMaxX(),SampleList[i].getX());
	    	int y=locationMapping(boundary.getMinY(),boundary.getMaxY(),SampleList[i].getY());
	    	hValues[i] = computeHValue(gridResolution+1,x,y);
	    }
	    createFromHValues(hValues, partitions);
	    for(int i=0;i<SampleList.length;i++)
	    {
	    	Envelope initialBoundary=new Envelope(SampleList[i].getX(),SampleList[i].getX(),SampleList[i].getY(),SampleList[i].getY());
	    	int partitionID=gridID(boundary,SampleList[i],splits);
	    	gridWithoutID[partitionID]=initialBoundary;
	    }
	    for(int i=0;i<SampleList.length;i++)
	    {
	    	int partitionID=gridID(boundary,SampleList[i],splits);
	    	gridWithoutID[partitionID]=updateEnvelope(gridWithoutID[partitionID],SampleList[i]);
	    }
	    for(int i=0;i<gridWithoutID.length;i++)
	    {
	    	gridWithID.add(new EnvelopeWithGrid(gridWithoutID[i],i));
	    }
	    //gridWithID.add(new EnvelopeWithGrid(boundary,gridWithID.size()));
	    this.grids=gridWithID;
	}
	
	/**
	 * Instantiates a new hilbert partitioning.
	 *
	 * @param SampleList the sample list
	 * @param boundary the boundary
	 * @param partitions the partitions
	 */
	public HilbertPartitioning(Envelope[] SampleList,Envelope boundary,int partitions)
	{
		//this.boundary=boundary;
		int gridResolution=Short.MAX_VALUE;
	    int[] hValues = new int[SampleList.length];
	    Envelope [] gridWithoutID=new Envelope[partitions];
	    HashSet<EnvelopeWithGrid> gridWithID= new HashSet<EnvelopeWithGrid>();
	    for (int i = 0; i < SampleList.length; i++){
	    	int x=locationMapping(boundary.getMinX(),boundary.getMaxX(),(SampleList[i].getMinX()+SampleList[i].getMaxX())/2.0);
	    	int y=locationMapping(boundary.getMinY(),boundary.getMaxY(),(SampleList[i].getMinY()+SampleList[i].getMaxY())/2.0);
	    	hValues[i] = computeHValue(gridResolution+1,x,y);
	    }
	      createFromHValues(hValues, partitions);
		    for(int i=0;i<SampleList.length;i++)
		    {
		    	Envelope initialBoundary=new Envelope(SampleList[i]);
		    	int partitionID=gridID(boundary,SampleList[i],splits);
		    	gridWithoutID[partitionID]=initialBoundary;
		    }
		    for(int i=0;i<SampleList.length;i++)
		    {
		    	int partitionID=gridID(boundary,SampleList[i],splits);
		    	gridWithoutID[partitionID]=updateEnvelope(gridWithoutID[partitionID],SampleList[i]);
		    }
		    for(int i=0;i<gridWithoutID.length;i++)
		    {
		    	gridWithID.add(new EnvelopeWithGrid(gridWithoutID[i],i));
		    }
		    //gridWithID.add(new EnvelopeWithGrid(boundary,gridWithID.size()));
		    this.grids=gridWithID;
	}
	
	/**
	 * Instantiates a new hilbert partitioning.
	 *
	 * @param SampleList the sample list
	 * @param boundary the boundary
	 * @param partitions the partitions
	 */
	public HilbertPartitioning(Polygon[] SampleList,Envelope boundary,int partitions)
	{
		//this.boundary=boundary;
		int gridResolution=Short.MAX_VALUE;
	    int[] hValues = new int[SampleList.length];
	    Envelope [] gridWithoutID=new Envelope[partitions];
	    HashSet<EnvelopeWithGrid> gridWithID= new HashSet<EnvelopeWithGrid>();
	    for (int i = 0; i < SampleList.length; i++){
	      Envelope envelope=SampleList[i].getEnvelopeInternal();
	      int x=locationMapping(boundary.getMinX(),boundary.getMaxX(),(envelope.getMinX()+envelope.getMaxX())/2.0);
	      int y=locationMapping(boundary.getMinY(),boundary.getMaxY(),(envelope.getMinY()+envelope.getMaxY())/2.0);
	      hValues[i] = computeHValue(gridResolution+1,x,y);
	    }
	      createFromHValues(hValues, partitions);
		    for(int i=0;i<SampleList.length;i++)
		    {
		    	Envelope initialBoundary=new Envelope(SampleList[i].getEnvelopeInternal());
		    	int partitionID=gridID(boundary,SampleList[i],splits);
		    	gridWithoutID[partitionID]=initialBoundary;
		    }
		    for(int i=0;i<SampleList.length;i++)
		    {
		    	int partitionID=gridID(boundary,SampleList[i],splits);
		    	gridWithoutID[partitionID]=updateEnvelope(gridWithoutID[partitionID],SampleList[i]);
		    }
		    for(int i=0;i<gridWithoutID.length;i++)
		    {
		    	gridWithID.add(new EnvelopeWithGrid(gridWithoutID[i],i));
		    }
		    //gridWithID.add(new EnvelopeWithGrid(boundary,gridWithID.size()));
		    this.grids=gridWithID;
	    }
	

	  /**
  	 * Creates the from H values.
  	 *
  	 * @param hValues the h values
  	 * @param partitions the partitions
  	 */
	  protected void createFromHValues(int[] hValues, int partitions) {
	    Arrays.sort(hValues);

	    this.splits = new int[partitions];
	    int maxH = 0x7fffffff;
	    for (int i = 0; i < splits.length; i++) {
	      int quantile = (int) ((long)(i + 1) * hValues.length / partitions);
	      this.splits[i] = quantile == hValues.length ? maxH : hValues[quantile];
	    }
	  }
	
	/**
	 * Compute H value.
	 *
	 * @param n the n
	 * @param x the x
	 * @param y the y
	 * @return the int
	 */
	  public static int computeHValue(int n, int x, int y) {
	    int h = 0;
	    for (int s = n/2; s > 0; s/=2) {
	      int rx = (x & s) > 0 ? 1 : 0;
	      int ry = (y & s) > 0 ? 1 : 0;
	      h += s * s * ((3 * rx) ^ ry);

	      // Rotate
	      if (ry == 0) {
	        if (rx == 1) {
	          x = n-1 - x;
	          y = n-1 - y;
	        }

	        //Swap x and y
	        int t = x; x = y; y = t;
	      }
	    }
	    return h;
	  }
	  
  	/**
	   * Gets the partition bounds.
	   *
	   * @return the partition bounds
	   */
  	public int[] getPartitionBounds()
	  {
		  return splits;
	  }
	  
  	/**
	   * Location mapping.
	   *
	   * @param axisMin the axis min
	   * @param axisLocation the axis location
	   * @param axisMax the axis max
	   * @return the int
	   */
  	public static int locationMapping (double axisMin, double axisLocation,double axisMax)
	  {
		  Double gridLocation;
		  int gridResolution=Short.MAX_VALUE;
		  gridLocation=(axisLocation-axisMin)*gridResolution/(axisMax-axisMin);
		  return gridLocation.intValue();
	  }
	  
  	/**
	   * Grid ID.
	   *
	   * @param boundary the boundary
	   * @param point the point
	   * @param partitionBounds the partition bounds
	   * @return the int
	   */
  	//The following three methods are used in RDD tuple-wise function
	  public static int gridID(Envelope boundary,Point point,int[] partitionBounds) {
		  int x=locationMapping(boundary.getMinX(),boundary.getMaxX(),point.getX());
		  int y=locationMapping(boundary.getMinY(),boundary.getMaxY(),point.getY());
		  int gridResolution=Short.MAX_VALUE;
		  int hValue = computeHValue(gridResolution+1,x,y);
		    int partition = Arrays.binarySearch(partitionBounds, hValue);
		    if (partition < 0)
		      partition = -partition - 1;
		    return partition;
		  }
	  
  	/**
	   * Grid ID.
	   *
	   * @param boundary the boundary
	   * @param envelope the envelope
	   * @param partitionBounds the partition bounds
	   * @return the int
	   */
  	public static int gridID(Envelope boundary,Envelope envelope,int[] partitionBounds) {
		  int x=locationMapping(boundary.getMinX(),boundary.getMaxX(),(envelope.getMinX()+envelope.getMaxX())/2.0);
		  int y=locationMapping(boundary.getMinY(),boundary.getMaxY(),(envelope.getMinY()+envelope.getMaxY())/2.0);
		  int gridResolution=Short.MAX_VALUE;
		  int hValue = computeHValue(gridResolution+1,x,y);
		    int partition = Arrays.binarySearch(partitionBounds, hValue);
		    //assert partition>=0;
		    if (partition < 0)
		      partition = -partition - 1;
		    return partition;
		  }
	  
  	/**
	   * Grid ID.
	   *
	   * @param boundary the boundary
	   * @param polygon the polygon
	   * @param partitionBounds the partition bounds
	   * @return the int
	   */
  	public static int gridID(Envelope boundary,Polygon polygon,int[] partitionBounds) {
		  Envelope envelope=polygon.getEnvelopeInternal();
		  int x=locationMapping(boundary.getMinX(),boundary.getMaxX(),(envelope.getMinX()+envelope.getMaxX())/2.0);
		  int y=locationMapping(boundary.getMinY(),boundary.getMaxY(),(envelope.getMinY()+envelope.getMaxY())/2.0);
		  int gridResolution=Short.MAX_VALUE;
		  int hValue = computeHValue(gridResolution+1,x,y);
		    int partition = Arrays.binarySearch(partitionBounds, hValue);
		    //assert partition>=0;
		    if (partition < 0)
		      partition = -partition - 1;
		    return partition;
		  }
		
		/**
		 * Update envelope.
		 *
		 * @param envelope the envelope
		 * @param i the i
		 * @return the envelope
		 */
		public static Envelope updateEnvelope(Envelope envelope, Point i)
		{
			double minX=envelope.getMinX();
			double maxX=envelope.getMaxX();
			double minY=envelope.getMinY();
			double maxY=envelope.getMaxY();
			if(minX>i.getX())
			{
				minX=i.getX();
			}
			if(maxX<i.getX())
			{
				maxX=i.getX();
			}
			if(minY>i.getY())
			{
				minY=i.getY();
			}
			if(maxY<i.getY())
			{
				maxY=i.getY();
			}
			return new Envelope(minX,maxX,minY,maxY);
		}
		
		/**
		 * Update envelope.
		 *
		 * @param envelope the envelope
		 * @param i the i
		 * @return the envelope
		 */
		public static Envelope updateEnvelope(Envelope envelope, Envelope i)
		{
			double minX=envelope.getMinX();
			double maxX=envelope.getMaxX();
			double minY=envelope.getMinY();
			double maxY=envelope.getMaxY();
			if(minX>i.getMinX())
			{
				minX=i.getMinX();
			}
			if(maxX<i.getMaxX())
			{
				maxX=i.getMaxX();
			}
			if(minY>i.getMinY())
			{
				minY=i.getMinY();
			}
			if(maxY<i.getMaxY())
			{
				maxY=i.getMaxY();
			}
			return new Envelope(minX,maxX,minY,maxY);
		}
		
		/**
		 * Update envelope.
		 *
		 * @param envelope the envelope
		 * @param polygon the polygon
		 * @return the envelope
		 */
		public static Envelope updateEnvelope(Envelope envelope, Polygon polygon)
		{
			double minX=envelope.getMinX();
			double maxX=envelope.getMaxX();
			double minY=envelope.getMinY();
			double maxY=envelope.getMaxY();
			Envelope i=polygon.getEnvelopeInternal();
			if(minX>i.getMinX())
			{
				minX=i.getMinX();
			}
			if(maxX<i.getMaxX())
			{
				maxX=i.getMaxX();
			}
			if(minY>i.getMinY())
			{
				minY=i.getMinY();
			}
			if(maxY<i.getMaxY())
			{
				maxY=i.getMaxY();
			}
			return new Envelope(minX,maxX,minY,maxY);
		}
		
		/**
		 * Gets the grids.
		 *
		 * @return the grids
		 */
		public HashSet<EnvelopeWithGrid> getGrids() {
			
			return this.grids;
			
		}
}
