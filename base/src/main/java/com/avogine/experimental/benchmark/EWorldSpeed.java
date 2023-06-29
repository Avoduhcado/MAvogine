package com.avogine.experimental.benchmark;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.BiConsumer;

import com.avogine.ecs.experimental.*;
import com.avogine.ecs.experimental.EntityChunk.TriConsumer;

/**
 *
 */
public class EWorldSpeed {
	private static record TComp() implements EntityComponent {}
	private static record MComp() implements EntityComponent {}
	private static record AComp() implements EntityComponent {}
	private static record PComp() implements EntityComponent {}
	
	private static Set<Class<? extends EntityComponent>> archetypeA = Set.of(TComp.class, MComp.class);
	private static Set<Class<? extends EntityComponent>> archetypeB = Set.of(TComp.class, MComp.class, PComp.class);
	
	private static int simulations = 500;

	private static record EntityThing(TComp t, MComp m, AComp a, PComp p) {}
	
	private static TMQuery tmQuery = new TMQuery();
	private static TMCachedQuery tmCachedQuery = new TMCachedQuery();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EntityWorld world = new EntityWorld();
		
		var arr = Array.newInstance(TComp.class, 512);
		System.out.println(arr.getClass().componentType());
		
		var biQ = new EntityBiQuery<TComp, MComp>() {
			@Override
			public void accept(TComp transform, MComp model) {
				if (transform == null || model == null) {
					System.out.println("PROBLEM");
				}
			}
		};
		
		System.out.println(biQ.getFirstType());
		System.out.println(biQ.getSecondType());
		System.out.println(tmCachedQuery.getFirstType());
		System.out.println(tmCachedQuery.getSecondType());
		
//		var quer = new EntityQuery<TComp> {
//			
//		}
//		System.out.println(quer.getClass().getRecordComponents()[0].getGenericType());
//		System.out.println(quer.getClass().getGenericSuperclass());
//		var thing = quer.getClass().getGenericSuperclass();
		
//		Consumer<> con = null;

		List<EntityThing> entities = new ArrayList<>();
		
		int aCount = 0;
		int bCount = 0;
		int cCount = 0;

		for (int j = 0; j < 3; j++) {
			world.getChunks().clear();
			world.getChunkLists().clear();
			entities.clear();
			for (int e = 0; e < 1000 * (Math.pow(10, j)); e++) {
				if (e % 3 == 0) {
					world.addEntity(new TComp(), new AComp());
					entities.add(new EntityThing(new TComp(), null, new AComp(), null));
					aCount++;
				} else if (e % 3 == 1) {
					world.addEntity(new TComp(), new MComp(), new PComp());
					entities.add(new EntityThing(new TComp(), new MComp(), null, new PComp()));
					bCount++;
				} else {
					world.addEntity(new TComp(), new MComp(), new AComp());
					entities.add(new EntityThing(new TComp(), new MComp(), new AComp(), null));
					cCount++;
				}
			}
			
			System.out.println("ACount:\t" + aCount + "\nBCount:\t" + bCount + "\nCCount:\t" + cCount);
			System.out.println("Chunks: " + world.getChunks().size());

			for (int i = 0; i < 5; i++) {
				//			if (i % 5 == 0) {
				//				for (int j = 0; j < 50; j++) {
				//					world.addEntity(new AComp(), new TComp(), new MComp());
				//				}
				//			}
				queryA(world);
				//			if (i % 2 == 0) {
				//				queryB(world);
				//				queryBench(world, archetypeB);
				//			} else {
				//				queryA(world);
				//				queryBench(world, archetypeA);
				//			}
				queryC(world);
				//			queryQuery(world);
				queryProcess(world);
				queryAll(world);
				//			queryList(world);
				//			queryListNoId(world);
				queryRegular(entities);

				System.out.println();
			}
		}
	}
	
	private static void queryBench(EntityWorld world, Set<Class<? extends EntityComponent>> archetype) {
		List<Long> benchTimes = new ArrayList<>();
		
		/*
		 * Stats in 5000 cycles with intermittent entity adds
		 * Raw Components:		111-129 micros
		 * Type Cast:			129-143 micros
		 * Class Cast:			133-152 micros
		 * Chunk Cast:			136-148 micros
		 * Chunk Arch:			400-600 micros Yuck!
		 * Cast Optional:		400-600 micros
		 * List with IDs:		2 millis Unacceptable
		 * List w/o IDs:		2.6 millis!
		 */
		
		for (int l = 0; l < simulations; l++) {
			long startTime = System.nanoTime();
			world.queryChunks(archetype)
			.forEach(chunk -> {
				for (int i = 0; i < chunk.getChunkSize(); i++) {
					// This is quick but what're you gonna do with a raw EntityComponent?
					// About 150 micros for 1 million entities querying for half
//					EntityComponent rawComponent = chunk.getComponents().get(TComp.class)[i];
					
					// This is really slow somehow
					// About 300 micros for 1 million entities querying for half
//					TComp componentSlow = (TComp) chunk.getComponents().get(TComp.class)[i];
					
					// This is slightly faster
					// About 290 micros for 1 million entities querying for half
//					TComp componentFaster = TComp.class.cast(chunk.getComponents().get(TComp.class)[i]);
					
					// This is about as slow as the (Type) cast
					// About 150 micros for 1 million entities querying for half. Not sure why this fluctuates, I assume it's memory positioning?
					if (archetype.contains(TComp.class)) {
						TComp t = chunk.getAs(TComp.class, i);
					}
					if (archetype.contains(MComp.class)) {
						MComp m = chunk.getAs(MComp.class, i);
					}
					if (archetype.contains(AComp.class)) {
						AComp a = chunk.getAs(AComp.class, i);
					}
					if (archetype.contains(PComp.class)) {
						PComp p = chunk.getAs(PComp.class, i);
					}
					
					// Similar speeds to using a consumer to auto cast things for us in a function
//					Optional<TComp> t = chunk.getAsOpt(TComp.class, i);
//					Optional<AComp> a = chunk.getAsOpt(AComp.class, i);
					
//					if (t == null) {
//						System.out.println("Problem!");
//					}
				}
			});
			benchTimes.add(System.nanoTime() - startTime);
		}
		
		System.out.println("queryBench:\t" + NumberFormat.getIntegerInstance().format(benchTimes.stream().skip(10).mapToLong(Long::valueOf).average().orElseThrow()) + " ns");
	}
	
	private static void queryA(EntityWorld world) {
		List<Long> benchTimes = new ArrayList<>();
		
		for (int l = 0; l < simulations; l++) {
			long startTime = System.nanoTime();
			world.queryChunks(archetypeA)
			.forEach(chunk -> {
				for (int i = 0; i < chunk.getChunkSize(); i++) {
					var t = chunk.getAs(TComp.class, i);
					var m = chunk.getAs(MComp.class, i);
//					chunk.getAsArch(TComp.class, AComp.class, (t, a) -> {
//						if (t == null || a == null) {
//							System.out.println("PROBLEM");
//						}
//					}, i);
				}
			});
			benchTimes.add(System.nanoTime() - startTime);
		}
		
		System.out.println("Chunks getAs:\t\t" + NumberFormat.getIntegerInstance().format(benchTimes.stream().skip(10).mapToLong(Long::valueOf).average().orElseThrow()) + " ns");
	}
	
	private static void queryB(EntityWorld world) {
		List<Long> benchTimes = new ArrayList<>();
		
		for (int l = 0; l < simulations; l++) {
			long startTime = System.nanoTime();
			world.queryChunks(archetypeB)
			.forEach(chunk -> {
				for (int i = 0; i < chunk.getChunkSize(); i++) {
//					var t = chunk.getAs(TComp.class, i);
					chunk.getAsArch(TComp.class, MComp.class, consumeTM, i);
				}
			});
			benchTimes.add(System.nanoTime() - startTime);
		}
		
		System.out.println("queryB:\t\t" + NumberFormat.getIntegerInstance().format(benchTimes.stream().skip(10).mapToLong(Long::valueOf).average().orElseThrow()) + " ns");
	}
	
	private static record RenderQuery(Class<? extends EntityComponent> tCompClass, Class<? extends EntityComponent> mCompClass) {}
	
	private static void queryC(EntityWorld world) {
		List<Long> benchTimes = new ArrayList<>();
		
		for (int l = 0; l < simulations; l++) {
			long startTime = System.nanoTime();
			world.queryChunks(TComp.class, MComp.class, AComp.class)
			.forEach(chunk -> {
				for (int i = 0; i < chunk.getChunkSize(); i++) {
					chunk.getAsArch(TComp.class, MComp.class, AComp.class, trisumerTMA, i);
				}
			});
			benchTimes.add(System.nanoTime() - startTime);
		}
		
		System.out.println("Chunks getAsArch:\t" + NumberFormat.getIntegerInstance().format(benchTimes.stream().skip(10).mapToLong(Long::valueOf).average().orElseThrow()) + " ns");
	}
	
	private static void queryQuery(EntityWorld world) {
		List<Long> benchTimes = new ArrayList<>();
		
		for (int l = 0; l < simulations; l++) {
			long startTime = System.nanoTime();
			world.query(tmCachedQuery)
			.forEach(chunk -> {
				for (int i = 0; i < chunk.getChunkSize(); i++) {
//					chunk.getAsArch(tmCachedQuery.getFirstType(), tmCachedQuery.getSecondType(), tmCachedQuery, i);
				}
			});
			benchTimes.add(System.nanoTime() - startTime);
		}
		
//		System.out.println("queryQuery:\t" + NumberFormat.getIntegerInstance().format(benchTimes.stream().skip(10).mapToLong(Long::valueOf).average().orElseThrow()) + " ns");
	}
	
	private static void queryProcess(EntityWorld world) {
		List<Long> benchTimes = new ArrayList<>();
		
		for (int l = 0; l < simulations; l++) {
			long startTime = System.nanoTime();
			world.query(tmCachedQuery)
			.forEach(chunk -> {
				for (int i = 0; i < chunk.getChunkSize(); i++) {
					chunk.processGeneric(tmCachedQuery, i);
				}
			});
			benchTimes.add(System.nanoTime() - startTime);
		}
		
		System.out.println("Query Process:\t\t" + NumberFormat.getIntegerInstance().format(benchTimes.stream().skip(10).mapToLong(Long::valueOf).average().orElseThrow()) + " ns");
	}
	
	private static void queryAll(EntityWorld world) {
		List<Long> benchTimes = new ArrayList<>();
		
		for (int l = 0; l < simulations; l++) {
			long startTime = System.nanoTime();
			world.queryAndProcess(tmCachedQuery);
			benchTimes.add(System.nanoTime() - startTime);
		}
		
		System.out.println("QueryAndProcess:\t" + NumberFormat.getIntegerInstance().format(benchTimes.stream().skip(10).mapToLong(Long::valueOf).average().orElseThrow()) + " ns");
	}

	private static void queryList(EntityWorld world) {
		List<Long> benchTimes = new ArrayList<>();
		
		for (int l = 0; l < simulations; l++) {
			long startTime = System.nanoTime();
			world.queryLists(archetypeA)
			.forEach(entityMap -> {
				EntityComponent t = entityMap.getValue().get(TComp.class);
				EntityComponent m = entityMap.getValue().get(MComp.class);
			});
			benchTimes.add(System.nanoTime() - startTime);
		}
		
		System.out.println("queryList:\t" + NumberFormat.getIntegerInstance().format(benchTimes.stream().skip(10).mapToLong(Long::valueOf).average().orElseThrow()) + " ns");
	}
	
	private static void queryListNoId(EntityWorld world) {
		List<Long> benchTimes = new ArrayList<>();
		
		for (int l = 0; l < simulations; l++) {
			long startTime = System.nanoTime();
			world.queryListsNoId(archetypeA)
			.forEach(entityMap -> {
				EntityComponent t = entityMap.get(TComp.class);
				EntityComponent m = entityMap.get(MComp.class);
			});
			benchTimes.add(System.nanoTime() - startTime);
		}
		
		System.out.println("queryListNoId:\t" + NumberFormat.getIntegerInstance().format(benchTimes.stream().skip(10).mapToLong(Long::valueOf).average().orElseThrow()) + " ns");
	}
	
	private static void queryRegular(List<EntityThing> entities) {
		List<Long> benchTimes = new ArrayList<>();
		
		for (int l = 0; l < simulations; l++) {
			long startTime = System.nanoTime();
			entities.stream().filter(et -> et.t != null && et.m != null && et.a != null).forEach(et -> {
				if (et.t == null || et.m == null || et.a == null) {
					System.out.println("PROBLEM");
				}
			});
			benchTimes.add(System.nanoTime() - startTime);
		}
		
		System.out.println("List filter:\t\t" + NumberFormat.getIntegerInstance().format(benchTimes.stream().skip(10).mapToLong(Long::valueOf).average().orElseThrow()) + " ns");
	}
	
	private static class TMQuery extends EntityBiQuery<TComp, MComp> {
		@Override
		public void accept(TComp t, MComp m) {
			if (t == null || m == null) {
				System.out.println("PROBLEM");
			}
		}
	}
	
	private static class TMCachedQuery extends EntityBiQuery<TComp, MComp> {
		@Override
		public void accept(TComp t, MComp m) {
			if (t == null || m == null) {
				System.out.println("PROBLEM");
			}
		}
		
	}
	
	private static final BiConsumer<TComp, MComp> consumeTM = (t, m) -> {
		if (t == null || m == null) {
			System.out.println("PROBLEM");
		}
	};
	
	private static final TriConsumer<TComp, MComp, AComp> trisumerTMA = (t, m, a) -> {
		if (t == null || m == null || a == null) {
			System.out.println("PROBLEM");
		}
	};
}
