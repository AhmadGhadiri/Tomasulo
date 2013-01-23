package simulator;
/**
 * This class represent Common Data Bus
 * it has a result for the value, and 
 * a Src for the reservation station that
 * it receives the result from
 * @author Ahmad
 *
 */

public class CDB {
	private long cdbResult;
	private String cdbSrc;
	public long getResult() {
		return cdbResult;
	}
	public void setResult(long result) {
		this.cdbResult = result;
	}
	public String getSrc() {
		return cdbSrc;
	}
	public void setSrc(String stationName) {
		this.cdbSrc = stationName;
	}
}
