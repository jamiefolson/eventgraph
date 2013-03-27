package net.sf.eventgraphj.tools;

import java.awt.Color;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.BlockRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.correlation.Covariance;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class StatisticConsistency {

	public static final String HELP = "help";
	public static final String FILE = "f";
	public static final String TIMESCALE = "scale";
	public static final String OUTPUT = "output";
	public static final String POOL_SIZE = "poolSize";
	public static final int WAIT_TIME = 100;
	public static final String CORRELATION_KEY = "Pearson's Correlation";
	public static final String CRONBACH_KEY = "Cronbach's Alpha";
	public static final String INFOLOSS_KEY = "% Information Loss";

	public static void main(String[] args) throws IOException {
		Options options = new Options();
		options.addOption(new Option(HELP, "print this message"));
		options.addOption(OptionBuilder.withArgName("file").hasArg()
		        .withDescription("use given file to construct graph").create(FILE));
		options.addOption(OptionBuilder
		        .withArgName("length")
		        .hasArg()
		        .withDescription(
		                "use given length of time as the basis for constructing exponentially larger temporal snapshots")
		        .create(TIMESCALE));
		options.addOption(OptionBuilder
		        .withArgName("path")
		        .hasArg()
		        .withDescription(
		                "put generated files into the given path, files contained there will be overwritten on collisions")
		        .create(OUTPUT));
		options.addOption(OptionBuilder.withArgName("n").hasArg()
		        .withDescription("use the given number of threads to run analysis").create(POOL_SIZE));

		// create the parser
		CommandLineParser parser = new GnuParser();
		CommandLine commandLineInput;
		// parse the command line arguments
		try {
			commandLineInput = parser.parse(options, args);

			if (commandLineInput.hasOption(HELP)) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("SnapshotStatistics -f <file>  [...]", options);
			} else if (!commandLineInput.hasOption(FILE)) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("SnapshotStatistics -f <file>  [...]", options);
			} else {
				String filename = commandLineInput.getOptionValue(FILE);
				Long timescale = Long.parseLong(commandLineInput.getOptionValue(TIMESCALE));

				String output = filename.substring(0, filename.lastIndexOf('.'));

				if (commandLineInput.hasOption(OUTPUT))
					output = commandLineInput.getOptionValue(OUTPUT);

				Integer poolSize = 0;
				if (commandLineInput.hasOption(POOL_SIZE)) {
					poolSize = Integer.parseInt(commandLineInput.getOptionValue(POOL_SIZE));
				}
				//SnapshotStatistics.computeStatistics(comparableGraph, output, timescale, poolSize);

				String dirname = "./";
				if (filename.lastIndexOf(File.separator) >= 0)
					dirname = filename.substring(0, filename.lastIndexOf(File.separator));

				final String basename = filename.substring(filename.lastIndexOf(File.separator) + 1);
				File directory = new File(dirname);
				String[] dataFilenames = directory.list(new FilenameFilter() {

					@Override
					public boolean accept(File arg0, String arg1) {
						if (arg1.startsWith(basename))
							return true;
						return false;
					}

				});

				HashMap<String, XYSeriesCollection> seriesMap = new HashMap<String, XYSeriesCollection>();
				Map<Double, Double> infoLoss = new HashMap<Double, Double>();
				double maxInfoLoss = Double.NEGATIVE_INFINITY;
				for (String dataFilename : dataFilenames) {
					String[] metaInfo = dataFilename.split("_");
					Double binsize = Double.parseDouble(metaInfo[1]);
					String statistic = metaInfo[2].replaceAll(".txt", "");
					System.out.println(dataFilename + " contains binsize " + binsize + " for statistic " + statistic);
					Scanner reader = new Scanner(new File(directory, dataFilename));
					List<RealVector> allVectors = new ArrayList<RealVector>();
					int N = -1;
					if (statistic.equals("infoloss")) {
						double sum = 0;
						while (reader.hasNextLine()) {
							String line = reader.nextLine();
							String[] values = line.split(",");
							N = values.length;
							for (int i = 0; i < values.length; i++) {
								sum += Double.parseDouble(values[i]);
							}
						}
						if (sum > maxInfoLoss)
							maxInfoLoss = sum;
						infoLoss.put(binsize, sum);
						continue;
					}
					while (reader.hasNextLine()) {
						String line = reader.nextLine();
						String[] values = line.split(",");
						N = values.length;
						RealVector vectorData = new ArrayRealVector(values.length);
						for (int i = 0; i < values.length; i++) {
							vectorData.setEntry(i, Double.parseDouble(values[i]));
						}
						//System.out.println(vectorData);
						allVectors.add(vectorData);
					}
					if (N <= 0) {
						throw new IllegalArgumentException("Invalid format for statistics: No data");
					}
					int M = allVectors.size();
					RealMatrix matrixData = new BlockRealMatrix(N, M);
					for (int j = 0; j < allVectors.size(); j++) {
						matrixData.setColumnVector(j, allVectors.get(j));
					}
					System.out.println("Data matrix: " + N + " x " + M);
					//System.out.println(matrixData);
					Covariance covarianceObject = new Covariance(matrixData);
					PearsonsCorrelation correlationObject = new PearsonsCorrelation(covarianceObject);
					RealMatrix correlationMatrix = correlationObject.getCorrelationMatrix();
					System.out.println(correlationMatrix);
					double sum = 0;
					for (int i = 0; i < M; i++) {
						for (int j = 0; j < M; j++) {
							if (i == j)
								continue;
							sum += correlationMatrix.getEntry(i, j);
						}
					}
					double meanCorrelation = sum / (M * (M - 1));

					RealMatrix covarianceMatrix = covarianceObject.getCovarianceMatrix();
					System.out.println(covarianceMatrix);

					sum = 0;
					for (int i = 0; i < M; i++) {
						sum += covarianceMatrix.getEntry(i, i);
					}
					double[] rowSums = new double[N];
					for (int i = 0; i < N; i++) {
						rowSums[i] = StatUtils.sum(matrixData.getRow(i));
					}
					double alpha = (M / (M - 1)) * (1 - sum / StatUtils.variance(rowSums));
					System.out.println("for statistic " + statistic + " with binsize " + binsize
					        + ": \n\tmean correlation: " + meanCorrelation + "\n\tcronbach alpha: " + alpha);
					XYSeriesCollection measureSeriesCollection = seriesMap.get(statistic);
					if (measureSeriesCollection == null) {
						measureSeriesCollection = new XYSeriesCollection();
						measureSeriesCollection.addSeries(new XYSeries(CORRELATION_KEY));
						measureSeriesCollection.addSeries(new XYSeries(CRONBACH_KEY));
						seriesMap.put(statistic, measureSeriesCollection);
					}
					measureSeriesCollection.getSeries(CORRELATION_KEY).add((double) binsize, (double) meanCorrelation);
					measureSeriesCollection.getSeries(CRONBACH_KEY).add((double) binsize, (double) alpha);

				}

				XYSeries infoLossSeries = new XYSeries(INFOLOSS_KEY);
				for (Entry<Double, Double> entry : infoLoss.entrySet()) {
					infoLossSeries.add((double) entry.getKey(), entry.getValue() / maxInfoLoss);
				}

				for (Entry<String, XYSeriesCollection> entry : seriesMap.entrySet()) {
					String measureId = entry.getKey();
					XYSeriesCollection seriesCollection = entry.getValue();
					seriesCollection.addSeries(infoLossSeries);

					JFreeChart chart = ChartFactory.createXYLineChart("Consistency Under Temporal Aggregation of "
					        + measureId, "Temporal Aggregation/Snapshot Size", "Value", seriesCollection,
					        PlotOrientation.VERTICAL, true, true, false);

					XYPlot plot = chart.getXYPlot();
					NumberAxis domainAxis = new LogarithmicAxis("Temporal Aggregation/Snapshot Size (Log)");
					plot.setDomainAxis(domainAxis);
					chart.setBackgroundPaint(Color.white);

					// create and display a frame...
					ChartFrame frame = new ChartFrame("Test", chart);
					frame.pack();
					frame.setVisible(true);
					JOptionPane.showConfirmDialog(null, "See next plot");
					frame.dispose();
				}

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
}
