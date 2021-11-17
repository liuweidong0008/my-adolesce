package com.adolesce.server.bloomFilter;

import java.io.*;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 1、引入：
 * 		现在有大量的数据，而这些数据的大小已经远远超出了服务器的内存，现在再给你一个数据，如何判断给你的数据在不在其中。如果服务器的内存足够大，
 * 		那么用HashMap是一个不错的解决方案，理论上的时间复杂度可以达到O(1)，但是现在数据的大小已经远远超出了服务器的内存，所以无法使用HashMap，
 * 		这个时候就可以使用“布隆过滤器”来解决这个问题。但是还是同样的，会有一定的“误判率”。
 *
 * 2、什么是布隆过滤器?
 * 		布隆过滤器是一个叫“布隆”的人提出的，它本身是一个很长的二进制向量，既然是二进制的向量，那么显而易见的，存放的不是0，就是1。
 *	  例子：
 *	  1）、新建一个长度为16的布隆过滤器，本质就是一个容器，也可以理解为一个数组，里面的数据默认值都是0
 *	  2）、现在需要添加一个数据：我们通过某种计算方式，比如Hash1，计算出了Hash1(数据)=5，我们就把下标为5的格子改成1
 *	  3）、我们又通过某种计算方式，比如Hash2，计算出了Hash2(数据)=9，我们就把下标为9的格子改成1
 *	  4）、还是通过某种计算方式，比如Hash3，计算出了Hash3(数据)=2，我们就把下标为2的格子改成1
 *	  5）、这样，刚才添加的数据就占据了布隆过滤器“5”，“9”，“2”三个格子。
 *	  6）、可以看出，仅仅从布隆过滤器本身而言，根本没有存放完整的数据，只是运用一系列随机映射函数计算出位置，然后填充二进制向量。
 *	  7）、比如现在再给你一个数据，你要判断这个数据是否重复，你怎么做？
 *	  8）、你只需利用上面的三种固定的计算方式，计算出这个数据占据哪些格子，然后看看这些格子里面放置的是否都是1，如果有一个格子不为1，那么就代表这个数字不在其中。
 *	  	  这很好理解，比如现在又给你了刚才你添加进去的数据，你通过三种固定的计算方式，算出的结果肯定和上面的是一模一样的，也是占据了布隆过滤器“5”，“9”，“2”三个格子。
 *	  9）、但是有一个问题需要注意，如果这些格子里面放置的都是1，不一定代表给定的数据一定重复，也许其他数据经过三种固定的计算方式算出来的结果也是相同的。
 *	      这也很好理解吧，比如我们需要判断对象是否相等，是不可以仅仅判断他们的哈希值是否相等的。
 *    10）、也就是说布隆过滤器只能判断数据是否一定不存在，而无法判断数据是否一定存在。
 *    11）、按理来说，介绍完了新增、查询的流程，就要介绍删除的流程了，但是很遗憾的是布隆过滤器是很难做到删除数据的，为什么？
 *         你想想，比如你要删除刚才给你的数据，你把“5”，“9”，“2”三个格子都改成了0，但是可能其他的数据也映射到了“5”，“9”，“2”三个格子啊，这不就乱套了吗？
 *
 *  3、优缺点
 *    1）、优点：由于存放的不是完整的数据，所以占用的内存很少，而且新增，查询速度够快；
 * 	  2）、缺点： 随着数据的增加，误判率随之增加；无法做到删除数据；只能判断数据是否一定不存在，而无法判断数据是否一定存在。
 *
 *	4、关于二进制向量容器长度：
 *		我举的例子二进制向量长度为16，由三个随机映射函数计算位置，在实际开发中，如果你要添加大量的数据，仅仅16位是远远不够的，
 *		为了让误判率降低，我们还可以用更多的随机映射函数、更长的二进制向量去计算位置。
 *
 *	5、布隆过滤器使用方式：
 *		1）、自定义：当前类就是自定义布隆过滤器
 *		2）、集成谷歌的布隆过滤器
 *		3）、redis实现（解决分布式布隆过滤器共享问题）
 *
 */
public class BloomFileter implements Serializable {
	private static final long serialVersionUID = -5221305273707291280L;
	//每个数据需要进行hash运算的位数（4位、8位、16位、32位）
	private final int[] seeds;
	//二进制向量容器的大小（BitSet容量） = seeds.length * 需要判重的数据个数dataCount
	private final int size;
	//二进制向量容器
	private final BitSet notebook;
	//hash运算策略的数组枚举
	private final MisjudgmentRate rate;
	//已运算的次数
	private final AtomicInteger useCount = new AtomicInteger(0);
	//当容器装载率到达这个比例时进行自动清理
	private final Double autoClearRate;
 
	/**
	 * 默认中等程序的误判率：MisjudgmentRate.MIDDLE 以及不自动清空数据（性能会有少许提升）
	 * 
	 * @param dataCount
	 *            预期处理的数据规模，如预期用于处理1百万数据的查重，这里则填写1000000
	 */
	public BloomFileter(int dataCount) {
		this(MisjudgmentRate.MIDDLE, dataCount, null);
	}
 
	/**
	 * 
	 * @param rate
	 *            一个枚举类型的误判率
	 * @param dataCount
	 *            预期处理的数据规模，如预期用于处理1百万数据的查重，这里则填写1000000
	 * @param autoClearRate
	 *            自动清空过滤器内部信息的使用比率，传null则表示不会自动清理，
	 *            当过滤器使用率达到100%时，则无论传入什么数据，都会认为在数据已经存在了
	 *            当希望过滤器使用率达到80%时自动清空重新使用，则传入0.8
	 */
	public BloomFileter(MisjudgmentRate rate, int dataCount, Double autoClearRate) {
		//二进制向量容器的大小（BitSet容量）
		long bitSize = rate.seeds.length * dataCount;
		if (bitSize < 0 || bitSize > Integer.MAX_VALUE) {
			throw new RuntimeException("位数太大溢出了，请降低误判率或者降低数据大小");
		}
		//hash运算策略的数组枚举
		this.rate = rate;
		//每个数据需要进行hash运算的位数（4位、8位、16位、32位）
		seeds = rate.seeds;
		//二进制向量容器的大小（BitSet容量）
		size = (int) bitSize;
		//二进制向量容器
		notebook = new BitSet(size);
		this.autoClearRate = autoClearRate;
	}

	/**
	 * 往二进制向量容器中添加数据
	 * @param data
	 */
	public void add(String data) {
		checkNeedClear();
		for (int i = 0; i < seeds.length; i++) {
			int index = hash(data, seeds[i]);
			setTrue(index);
		}
	}

	/**
	 * 检测二进制向量容器中是否已经有该数据了，如果存在返回false，不存在返回true
	 * @param data
	 * @return
	 */
	public boolean check(String data) {
		for (int i = 0; i < seeds.length; i++) {
			int index = hash(data, seeds[i]);
			if (!notebook.get(index)) {
				return true;
			}
		}
		return false;
	}
 
	/**
	 * 如果二进制向量容器中不存在就进行添加并返回true，如果存在了就返回false
	 * 
	 * @param data
	 * @return
	 */
	public boolean addIfNotExist(String data) {
		//检测是否需要对向量容器进行清理
		checkNeedClear();
 		//定义一个数组，长度与hash策略数组长度一致
		int[] indexs = new int[seeds.length];
		// 先假定存在
		boolean exist = false;
		int index;
		//循环hash策略数组,每次循环得到一个hash值
		for (int i = 0; i < seeds.length; i++) {
			//对策略数组元素与传入的数据进行hash运算，得到一个hash值
			indexs[i] = index = hash(data, seeds[i]);

			if (!exist) {
				//对二进制向量数组中该角标位置是否存在数据进行判断
				if (!notebook.get(index)) {
					//只要有一个不存在，就可以认为整个字符串都是第一次出现的
					exist = true;
					//将二进制向量数组中该位置标注为true,即1
					setTrue(index);
					// 补充之前的信息
					/*for (int j = 0; j <= i; j++) {
						setTrue(indexs[j]);
					}*/
				}
			} else {
				setTrue(index);
			}
		}
		return exist;
	}

	/**
	 * 检测是否需要对向量容器进行清理
	 */
	private void checkNeedClear() {
		if (autoClearRate != null) {
			//当容器使用率大于等于设定的比例，对向量容器进行情况
			if (getUseRate() >= autoClearRate) {
				//多线程情况需要同步加锁保证数据安全
				synchronized (this) {
					if (getUseRate() >= autoClearRate) {
						notebook.clear();
						useCount.set(0);
					}
				}
			}
		}
	}

	/**
	 * 将向量容器中指定角标位置设置为true，使用率自增1
	 * @param index
	 */
	public void setTrue(int index) {
		useCount.incrementAndGet();
		notebook.set(index, true);
	}
 
	private int hash(String data, int seeds) {
		char[] value = data.toCharArray();
		int hash = 0;
		if (value.length > 0) {
 
			for (int i = 0; i < value.length; i++) {
				hash = i * hash + value[i];
			}
		}
		hash = hash * seeds % size;
		// 防止溢出变成负数
		return Math.abs(hash);
	}

	/**
	 * 获取向量容器使用率
	 * @return
	 */
	public double getUseRate() {
		return (double) useCount.intValue() / (double) size;
	}

	/**
	 * 将布隆过滤器对象保存至文件中（可用于项目关闭，spring容器销毁前）
	 * @param path
	 */
	public void saveFilterToFile(String path) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
			oos.writeObject(this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
 
	}

	/**
	 * 从文件中读取数据至布隆过滤器对象中（可用于项目启动，spring容器初始化后）
	 * @param path
	 * @return
	 */
	public static BloomFileter readFilterFromFile(String path) {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
			return (BloomFileter) ois.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
 
	/**
	 * 清空过滤器中的记录信息
	 */
	public void clear() {
		useCount.set(0);
		notebook.clear();
	}
 
	public MisjudgmentRate getRate() {
		return rate;
	}
 
	/**
	 * 分配的位数越多，误判率越低但是越占内存
	 * 
	 * 4个位误判率大概是0.14689159766308
	 * 
	 * 8个位误判率大概是0.02157714146322
	 * 
	 * 16个位误判率大概是0.00046557303372
	 * 
	 * 32个位误判率大概是0.00000021167340
	 * 
	 * @author lianghaohui
	 *
	 */
	public enum MisjudgmentRate {
		// 这里要选取质数，能很好的降低错误率
		/**
		 * 每个字符串分配4个位
		 */
		VERY_SMALL(new int[] { 2, 3, 5, 7 }),
		/**
		 * 每个字符串分配8个位
		 */
		SMALL(new int[] { 2, 3, 5, 7, 11, 13, 17, 19 }),
		/**
		 * 每个字符串分配16个位
		 */
		MIDDLE(new int[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53 }),
		/**
		 * 每个字符串分配32个位
		 */
		HIGH(new int[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97,
				101, 103, 107, 109, 113, 127, 131 });
 
		private int[] seeds;
 
		private MisjudgmentRate(int[] seeds) {
			this.seeds = seeds;
		}
 
		public int[] getSeeds() {
			return seeds;
		}
 
		public void setSeeds(int[] seeds) {
			this.seeds = seeds;
		}
	}
}