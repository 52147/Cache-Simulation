# Cache-Simulation
## Description
- You are to design and implement a software simulation of a cache memory subsystem. 
- This will be a simulation of the way that a real cache works. 
- Your "cache" will be a software structure/class that will contain information similar (but on a smaller and more simplified scale) to what would be in a real cache. 
- Your "main memory" will be a 2K array. 
- You can make it an array of integers or shorts (16-bits), but because it is simulating a byte-addressable memory, you won't be putting any value larger than 0xFF (255 decimal or 11111111 binary) in it.
- You will provide for these two areas by defining an array for main memory and a structure/class/record (or array of these) for the cache. 
- For example, short Main_mem[2048];
- So, in effect, your Main_mem array will be the "pretend" main memory area, your cache structure/class which you define will be the "pretend" cache, your program will pretend to be the intelligence of the memory sub-system, and requests that you type in will be the equivalent of a processor making requests of the memory subsystem.
- You will implement **a direct-mapped, write-back cache.**
- The block size will be **16 bytes and you'll have 16 slots.** 
- Both of these numbers are smaller than a typical real cache, but make for a more manageable simulation. 
- You should use a structure/class to manage each slot, where the structure consists of the appropriate information (e.g., a valid flag, tag, etc.) and you eventually
have an array of these structures. 

## 1. 說明
更新於2023/1/3 : 要面試了，來整理回顧一下以前寫的作業!!    
Cache 是資料快取的地方，當CPU(processor)需要資料時，會先從cache裡尋找，cache裡沒找到才會從main memory裡找，這樣能減少讀取時間。   
Cache 的資料儲存方式是直接映射(direct-mapped) : 每一個main memory address經過運算都有對應的cache儲存位置。

這是一個cache模擬程式，模擬CPU對cache的操作，有三個function :
1. 讀取地址 : 
   - 先解析main memory 的field，找到 tag、slot、offset
   - 利用slot 來查詢 cache block 的valid bit是不是等於0，等於0代表沒有儲存地址，或tag不等於 cache block 的 tag，如果以上兩個條件有1個符合，代表cache miss，cache中沒有儲存memory address。
3. 寫入資料
4. 顯示資料

## 2. 流程圖

