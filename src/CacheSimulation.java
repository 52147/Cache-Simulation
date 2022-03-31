import java.util.*;

/**
 * Assignment: Computer Architecture Project 2 - Cache Simulation 
 * Class: CS 472
 * Due Date: March. 24. 2022 
 * Author: Shou-Tzu, Han 
 * Description: Create a array main memory and array cache to simulate the requests(read, write, display) from the processor.
 * 
 * 
 * memory address:
 * 
 * |  tag  |  index(slot field)  |  block offset  |
 * 
 *  memory size = 2k = 2048(2^11) bytes  -> memory address 11 bits
 *  block size = 16(2^4) bytes -> offset 4 bits
 *  number of cache slot = 16(2^4) -> slot field 4 bits
 *  tag = memory address - offset - index = 11 - 4 - 4 = 3 bits
 *  
 *  tag: 3 bits
 *  slot field: 4 bits
 *  offset: 4 bits
 *  
 *  tag : derived from the memory block address to identify whether a word in the cache corresponds to the requested word
 *  slot field : for select the cache block
 *  offset : is the address of the desired data within the block
 * 
 * 
 * Algorithms:
 * 
 * 0. findField method: find the field of tag, slot field, offset.
 * 1. read method: 
 *    - if cache miss:
 *      - find the input address field
 *      - use BITWISE ANDS to find the data from the address
 *      - put the data in the cache block
 *    - otherwise:  print cache hit
 *    - return data in the block
 * 2. write method:
 *    - call the read method to find the corresponding slot from the address 
 *    - write the input data in the block 
 *                       
 */

public class CacheSimulation {

	public static void main(String[] args) {
		CacheSimulation simulator = new CacheSimulation();
		simulator.operate();
	}

	public void operate() {

		Scanner operation = new Scanner(System.in);

		// array object "Cache" for 16 slots in cache
		Cache[] cache = new Cache[16];

		for (int i = 0; i < cache.length; i++) {
			cache[i] = new Cache();
		}

		// short array "mainMemory" for main memory size 2k = 2048 bytes
		short[] mainMemory = new short[2048];

		int value = 0;

		for (int i = 0; i < mainMemory.length; i++) {
			mainMemory[i] = (short) value;
			value++;
			// putting any value larger than 0xFF(255 decimal)
			if (value > 255) {
				value = 0;
			}
		}

		String input = "";

		do {
			// 3 operations: 1 read byte ; 2. write byte ; 3. display cache
			System.out.println("(r)ead, (w)rite, or (d)isplay Cache?");

			input = operation.next();

			switch (input.toLowerCase()) {

			case "r":

				// convert the address to hex
				short readAddress = convertAddressToHex();
				System.out.printf("%s%X%s", "At address ", readAddress, " there is the value: ", readAddress);
				System.out.printf("%X%n", read(cache, mainMemory, readAddress));

				break;

			case "w":

				short writeAddress = convertAddressToHex();

				System.out.println("What data would you like to wrie at that address? ");
				String inputData = operation.next();

				while (!checkAddress(inputData) || Integer.parseInt(inputData, 16) > 0xFF) {
					System.out.printf("%s%X%s", "Error. Please enter a hexadecimal number under ", 0x100, ": ");
					inputData = operation.next();
				}

				short data = (short) (Integer.parseInt(inputData, 16));

				System.out.printf("%s%X%s", "Value at ", writeAddress, " has been written to address: ");
				System.out.printf("%X%n", write(cache, mainMemory, writeAddress, data));

				break;

			case "d":

				display(cache);

				break;

			default:
				System.out.println("Please enter a valid operation.");

				break;
			}

		} while (input != "r" || input != "w" || input != "d");

		operation.close();

	}

	// return the tag, slot, offset of input address
	public short[] findField(short address) {
		short[] field = new short[3];

		short tag = (short) ((short) (address & 0xFF00) >>> 8);
		short slot = (short) ((short) (address & 0x00F0) >>> 4);
		short offset = (short) (address & 0x000F);

		field[0] = tag;
		field[1] = slot;
		field[2] = offset;

		return field;

	}

	// Read: check the cache hit or miss
	public int read(Cache[] c, short[] m, short address) {

		short[] f = findField(address);
		short tag = f[0];
		short slotField = f[1];
		short offset = f[2];

		Cache slot = c[slotField];

		// If address is not in the cache(miss)
		if (slot.isValid() == 0 || tag != slot.getTag()) {
			System.out.print("(CACHE MISS) ");

			// handling a miss of address
			if ((slot.isValid() == 1) && (slot.isDirtyBit() == 1)) {
				short[] values = slot.getBlock();

				short oldBlockBegine = (short) ((slot.getTag() << 8) + (slotField << 4));

				for (short i = 0; i < values.length; i++) {
					m[oldBlockBegine + i] = values[i];
				}
				slot.setDirtyBit(false);
				// The dirty bit would be false until you wrote to a given block in the cache.  
				// Then when it came time to overwrite a block, you'd only copy it back to main memory if the dirty bit was true.

			}

			// update the address in the cache
			short blockBegin = (short) (address & 0xFFF0);
			short[] block = new short[c.length];

			for (short i = 0; i < block.length; i++) {
				block[i] = m[blockBegin + i];
			}

			slot.setBlock(block);
			slot.setValid(true);
			slot.setTag(tag);
		}

		else {
			System.out.print("(CACHE HIT) ");
		}

		return slot.getData(offset);

	}

	// Write: when a write occurs, the new value is written only to the block in the
	// cache.
	public short write(Cache[] c, short[] m, short address, short data) {

		// find the data in the cache
		read(c, m, address);

		short[] a = findField(address);
		short slotField = a[1];
		short offset = a[2];

		Cache slot = c[slotField];
		
		slot.setDirtyBit(true);

		// write the input data in the block
		slot.setData(offset, data);

		return slot.getData(offset);
	}

	// Display the cache
	public void display(Cache[] c) {
		String output = "\nSlot Valid DirtyBit Tag		Data\n";
		// 		String rtn = "\nSlot Valid DirtyBit Tag		Data\n";


		short[] cacheBlock = new short[c[0].getBlock().length];

		for (short i = 0; i < c.length; i++) {
			cacheBlock = c[i].getBlock();
			output += String.format("%2X%6d%8d%6X", i, c[i].isValid(), c[i].isDirtyBit(), c[i].getTag());
			output += String.format("%12X%4X%4X%4X%4X%4X%4X%4X%4X%4X%4X%4X%4X%4X%4X%4X%n", cacheBlock[0], cacheBlock[1],
					cacheBlock[2], cacheBlock[3], cacheBlock[4], cacheBlock[5], cacheBlock[6], cacheBlock[7],
					cacheBlock[8], cacheBlock[9], cacheBlock[10], cacheBlock[11], cacheBlock[12], cacheBlock[13],
					cacheBlock[14], cacheBlock[15]);

		}
		System.out.print(output);
	}

	// check the address is hex
	public boolean checkAddress(String input) {
		try {
			short hex = (short) (Integer.parseInt(input, 16)); // return a hexadecimal integer(16 radix)
			return true;

		} catch (NumberFormatException e) {
			return false;
		}

	}

	// Convert the address to hex and return
	public short convertAddressToHex() {

		Scanner keyboard = new Scanner(System.in);

		String inputAddress;
		System.out.println("What address would you like to read to? ");
		inputAddress = keyboard.next();

		// check the input address is hex
		while (!checkAddress(inputAddress) || Integer.parseInt(inputAddress, 16) >= 0x800) {
			System.out.print("Error. Please enter a hexadecimal number under 800: ");
			inputAddress = keyboard.next();
		}

		short hexAddress = (short) (Integer.parseInt(inputAddress, 16));
		return hexAddress;

	}

}
