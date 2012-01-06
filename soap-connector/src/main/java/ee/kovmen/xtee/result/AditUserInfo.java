package ee.kovmen.xtee.result;

import java.math.BigInteger;

public class AditUserInfo {
	private boolean CanRead;

	public boolean getCanRead() {
		return this.CanRead;
	}

	public void setCanRead(boolean name) {
		this.CanRead = name;
	}
	
	private boolean CanWrite;

	public boolean getCanWrite() {
		return this.CanWrite;
	}

	public void setCanWrite(boolean name) {
		this.CanWrite = name;
	}
	
	private boolean UsesDVK;

	public boolean getUsesDVK() {
		return this.UsesDVK;
	}

	public void setUsesDVK(boolean name) {
		this.UsesDVK = name;
	}
	
	private boolean HasJoined;

	public boolean getHasJoined() {
		return this.HasJoined;
	}

	public void setHasJoined(boolean name) {
		this.HasJoined = name;
	}
	
	private BigInteger FreeSpaceBytes;

	public BigInteger getFreeSpaceBytes() {
		return this.FreeSpaceBytes;
	}

	public void setFreeSpaceBytes(BigInteger name) {
		this.FreeSpaceBytes = name;
	}
	
	private BigInteger UsedSpaceBytes;

	public BigInteger getUsedSpaceBytes() {
		return this.UsedSpaceBytes;
	}

	public void setUsedSpaceBytes(BigInteger name) {
		this.UsedSpaceBytes = name;
	}
}