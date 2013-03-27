package net.sf.eventgraphj.rjava;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.rosuda.JRI.Rengine;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.JRI.JRIEngine;

import net.sf.eventgraphj.comparable.NavigableGraph;
import net.sf.eventgraphj.tools.NavigableGraphBenchmark;
import net.sf.eventgraphj.tools.NavigableGraphBenchmark.Implementation;

public class DataFrameBenchmark<V> extends NavigableGraphBenchmark<V>{
	List<String> constructionTypes = new ArrayList<String>();
	List<Double> constructionTimes = new ArrayList<Double>();
	
	List<String> queryTypes = new ArrayList<String>();
	List<Double> querySize = new ArrayList<Double>();
	List<Double> queryTimes = new ArrayList<Double>();
	
		public DataFrameBenchmark(NavigableGraph<Long, V, String> baseGraph,Class<V> vertexType){
			super(baseGraph,vertexType);
			
		}

		protected void recordQueryTime(long thisinterval, String graphType,
				long queryTime) {
				queryTimes.add((double) queryTime);
				queryTypes.add(graphType);
				querySize.add((double) thisinterval);
		}


		protected void recordConstructionTime(String type, long constructionTime) {
			
			constructionTypes.add(type.toLowerCase());
			constructionTimes.add((double) constructionTime);
		}
		
		public RList close() throws REngineException, REXPMismatchException{
			REngine engine = new JRIEngine(Rengine.getMainEngine());
			
			RList resultList = new RList();
			RList constructList = new RList();
			constructList.put("type",new REXPString(constructionTypes.toArray(new String[]{})));
			double[] vals = new double[constructionTimes.size()];
			for (int i=0;i<vals.length;i++){
				vals[i] = constructionTimes.get(i);
			}
			constructList.put("time",new REXPDouble(vals));
			
			resultList.put("construction", REXP.createDataFrame(constructList));
			
			RList queryList = new RList();
			queryList.put("type",new REXPString(queryTypes.toArray(new String[]{})));
			
			vals = new double[queryTimes.size()];
			for (int i=0;i<vals.length;i++){
				vals[i] = queryTimes.get(i);
			}
			queryList.put("time",new REXPDouble(vals));

			vals = new double[queryTimes.size()];
			for (int i=0;i<vals.length;i++){
				vals[i] = querySize.get(i);
			}
			queryList.put("size",new REXPDouble(vals));
			
			resultList.put("query", REXP.createDataFrame(queryList));
			
			return resultList;

		}
		
	
}
