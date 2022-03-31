public class Cache {

	private boolean valid = false;
	private boolean dirtyBit = false;
	// a modified page is called a dirty page
	// the dirty bit indicates whether the page needs to be written out before its
	// location in memory can be given to another page.
	private short tag = 0;
	private short[] block = new short[16]; // cache block(offset) contains data

	// Use valid bit to indicate whether an entry contains a valid address.
	public int isValid() {
		if (valid) {
			return 1;
		} else {
			return 0;
		}
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
    
	// if dirty bit is true, which means block has been written to, return 1
	public int checkDirtyBit() {
		if (dirtyBit) {
			return 1;
		} else {
			return 0;
		}
	}

	public void setDirtyBit(boolean dirtyBit) {
		this.dirtyBit = dirtyBit;
	}

	// Get and Set tag
	public short getTag() {
		return tag;
	}

	public void setTag(short tag) {
		this.tag = tag;
	}

	// Get and set data
	public short getData(int offset) {
		return block[offset];
	}

	public void setData(short offset, short dataValue) {
		block[offset] = dataValue;
	}

	// Get and set block
	public short[] getBlock() {
		return block;
	}

	public void setBlock(short[] data) {
		this.block = data;
	}

}
