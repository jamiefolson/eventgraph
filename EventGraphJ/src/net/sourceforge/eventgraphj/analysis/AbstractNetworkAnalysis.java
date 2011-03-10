package net.sourceforge.eventgraphj.analysis;

import java.io.IOException;
import java.io.Writer;

import edu.uci.ics.jung.graph.Graph;

/**
 * An abstract class that begins to setup a common infrastructure for analyzing
 * networks and recording and retrieving the results of analysis.
 * 
 * @author jfolson
 * 
 * @param <V>
 * @param <E>
 * @param <G>
 * @param <R>
 */
public abstract class AbstractNetworkAnalysis<V, E, G extends Graph<V, E>, R> implements NetworkAnalysis<V, E, G> {
	protected Writer output = null;

	public Writer getOutput() {
		return output;
	}

	public void setOutput(Writer output) {
		this.output = output;
	}

	protected R result;

	public R getResult() {
		return result;
	}

	public AbstractNetworkAnalysis(Writer output) {
		this.output = output;
	}

	public AbstractNetworkAnalysis() {
		this(null);
	}

	public R analyze(G graph) {
		result = doAnalysis(graph);
		if (output != null && result != null) {
			try {
				this.writeResult(result, output);
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method is used to write the {@code result} to {@code output}
	 * allowing for custom formatting of the results rather than simple {@code
	 * Object.toString()}
	 * 
	 * @param result
	 * @param output
	 * @throws IOException
	 */
	protected void writeResult(R result, Writer output) throws IOException {
		output.write(result.toString());
	}

	public void close() throws IOException {
		if (output != null) {
			output.close();
		}
	}

	protected abstract R doAnalysis(G graph);
}
