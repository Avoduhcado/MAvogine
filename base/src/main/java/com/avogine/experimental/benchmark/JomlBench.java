package com.avogine.experimental.benchmark;

import java.util.*;
import java.util.Random;

import org.joml.*;

/**
 *
 */
public class JomlBench {

	private record NativeTransform(float x, float y, float z) {}
	private record RefTransform(Vector3f position) {}
	
	public static void main(String[] args) {
		List<NativeTransform> nativeTransforms = new ArrayList<>();
		List<RefTransform> refTransforms = new ArrayList<>();
		
		final Vector3f position = new Vector3f();
		final Matrix4f modelMatrix = new Matrix4f();
		
		Random rand = new Random();
		for (int i = 0; i < 10000; i++) {
			nativeTransforms.add(new NativeTransform(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
			refTransforms.add(new RefTransform(new Vector3f(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())));
		}
		
		int loops = 5000;
		
		List<Long> nativeTimes = new ArrayList<>();
		for (int i = 0; i < loops; i++) {
			long start = System.nanoTime();
			nativeTransforms.forEach(t -> modelMatrix.identity().translate(position.set(t.x(), t.y(), t.z())));
			nativeTimes.add(System.nanoTime() - start);
		}
		System.out.println("Native time avg:\t" + nativeTimes.stream().skip(5).mapToLong(Long::valueOf).average().orElseThrow());
		
		List<Long> nativeDirectTimes = new ArrayList<>();
		for (int i = 0; i < loops; i++) {
			long start = System.nanoTime();
			nativeTransforms.forEach(t -> modelMatrix.identity().translate(t.x(), t.y(), t.z()));
			nativeDirectTimes.add(System.nanoTime() - start);
		}
		System.out.println("Native Direct time avg:\t" + nativeDirectTimes.stream().skip(5).mapToLong(Long::valueOf).average().orElseThrow());
		
		List<Long> refTimes = new ArrayList<>();
		for (int i = 0; i < loops; i++) {
			long start = System.nanoTime();
			refTransforms.forEach(t -> modelMatrix.identity().translate(t.position()));
			refTimes.add(System.nanoTime() - start);
		}
		System.out.println("Reference time avg:\t" + refTimes.stream().skip(5).mapToLong(Long::valueOf).average().orElseThrow());
	}
	
}
