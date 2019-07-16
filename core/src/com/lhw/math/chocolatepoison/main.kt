@file:Suppress("NOTHING_TO_INLINE")

package com.lhw.math.chocolatepoison


fun main(args: Array<String>) {
	if(args.size == 2) {
		width = args[0].toInt()
		height = args[1].toInt()
	}
	
	mainGame()
}


var width: Int = 4
var height: Int = 4


fun mainGame() {
	println("START")
	val startTime = System.currentTimeMillis()
	val root = Root()
	root.evaluate(intArrayOf())
	println("best: ${root.findBest()}")
	println(root.children.contentToString())
	val endTime = System.currentTimeMillis()
	println("END, time took ${(endTime - startTime).toFloat() / 1000f}")
//	root.collectAbsoluteWinning()
}

//
//inline class Int(val id: Int) {
//	constructor(x: Int, y: Int) : this(x * width + y)
//	
//	inline val x: Int get() = id / width
//	inline val y: Int get() = id % width
//}

inline fun yOf(pos: Int): Int = pos % width
inline fun xOf(pos: Int): Int = pos / width
inline fun posOf(x: Int, y: Int) = y * width + x


val _tempArr = IntArray(width * height) { 0 }


open class Act(val pos: Int) {
	lateinit var children: Array<Act>
	
	@Suppress("UNCHECKED_CAST")
	fun evaluateAllPossibleNextActs(curStack: IntArray): IntArray {
		val arr = BooleanArray(width * height) { true }
		curStack.forEach {
			for(x in xOf(it) until width)
				for(y in 0..yOf(it))
					arr[posOf(x, y)] = false
		}
		arr[posOf(0, height - 1)] = false
		var capacity = 0
		for(i in 0 until width * height) if(arr[i]) {
			_tempArr[capacity] = i
			capacity++
		}
		return _tempArr.sliceArray(0 until capacity)
	}
//			(0.. { it } - curStack - posOf(0, height - 1)
	
	fun evaluate(curStack: IntArray) {
//		print("${"-".repeat(curStack.size)} ")
		children = evaluateAllPossibleNextActs(curStack).mapArray { newChild(it) }
		if(children.isEmpty()) {
//			println("#")
			return
		}

//		println(if(this is A) "A" else if(this is B) "B" else "?")
		
		val childStack = curStack + 0
		val lastChildStackIndex = curStack.size
		
		children.forEach {
			childStack[lastChildStackIndex] = it.pos
			it.evaluate(childStack)
		}
	}
	
	val successRatio: Float
		get() {
			if(children.isEmpty())
				return if(this is A) 1f else 0f
			
			var addAll = 0f
			for(item in children) addAll += item.successRatio
			return addAll / children.size
		}
	
	fun findBest(): List<Int> {
		var maxVal = -1f
		val maxPosition = mutableListOf<Int>()
		
		for(i in children) {
			if(i.successRatio >= maxVal) {
				maxVal = i.successRatio
				maxPosition += i.pos
			}
		}
		
		return maxPosition
	}
	
	fun dump(): String =
			"$pos: ${if(children.isEmpty()) if(this is A) "O" else "X" else children.map { it.dump() }.toString()}"
	
	override fun toString(): String {
		return pos.toString()
	}
	
	@Suppress("NOTHING_TO_INLINE")
	private inline fun newChild(pos: Int): Act = if(this is A) B(pos) else A(pos)
}

class Root : Act(-1)

class A(pos: Int) : Act(pos)

class B(pos: Int) : Act(pos)


inline fun <reified R> IntArray.mapArray(transform: (Int) -> R): Array<R> = Array(size) {
	transform(this[it])
}

inline fun <T, reified R> Array<T>.mapArray(transform: (T) -> R): Array<R> = Array(size) {
	transform(this[it])
}

inline fun <T, R> Array<T>.mapArrayTo(array: Array<R>, transform: (T) -> R) {
	for(i in 0..size) array[i] = transform(this[i])
}