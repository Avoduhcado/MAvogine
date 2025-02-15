package com.avogine.util;

import java.text.*;
import java.util.*;

import com.avogine.logging.AvoLog;

/**
 * Singleton for profiling the game loop.
 */
public enum FrameProfiler implements Profilable {

	/**
	 * Profiler to use when tracking game loop processing times.
	 */
	DEBUG {
		private final List<Long> frameNanos = new ArrayList<>();
		private final List<Long> inputNanos = new ArrayList<>();
		private final List<Long> updateNanos = new ArrayList<>();
		private final List<Long> renderNanos = new ArrayList<>();
		private final List<Long> budgetNanos = new ArrayList<>();

		private long frameStart;
		private long inputStart;
		private long updateStart;
		private long renderStart;
		private final DecimalFormat df = new DecimalFormat("###000,000.00μs");

		@Override
		public void startFrame() {
			frameStart = System.nanoTime();
		}

		@Override
		public void endFrame() {
			frameNanos.add(System.nanoTime() - frameStart);
		}

		@Override
		public void endBudget() {
			budgetNanos.add(System.nanoTime() - frameStart);
		}
		
		@Override
		public void inputStart() {
			inputStart = System.nanoTime();
		}
		
		@Override
		public void inputEnd() {
			inputNanos.add(System.nanoTime() - inputStart);
		}

		@Override
		public void updateStart() {
			updateStart = System.nanoTime();
		}

		@Override
		public void updateEnd() {
			updateNanos.add(System.nanoTime() - updateStart);
		}

		@Override
		public void renderStart() {
			renderStart = System.nanoTime();
		}

		@Override
		public void renderEnd() {
			renderNanos.add(System.nanoTime() - renderStart);
		}
		
		private double getFrameAverage() {
			double avg = frameNanos.stream().mapToLong(Long::valueOf).average().orElseThrow();
			frameNanos.clear();
			return avg;
		}
		
		private double getInputAverage() {
			double avg = inputNanos.stream().mapToLong(Long::valueOf).average().orElseThrow();
			inputNanos.clear();
			return avg;
		}
		
		private double getUpdateAverage() {
			double avg = updateNanos.stream().mapToLong(Long::valueOf).average().orElseThrow();
			updateNanos.clear();
			return avg;
		}
		
		private double getRenderAverage() {
			double avg = renderNanos.stream().mapToLong(Long::valueOf).average().orElseThrow();
			renderNanos.clear();
			return avg;
		}
		
		private double getBudgetAverage() {
			double avg = budgetNanos.stream().mapToLong(Long::valueOf).average().orElseThrow();
			budgetNanos.clear();
			return avg;
		}

		@Override
		public void printAverages() {
			double frames = getFrameAverage();
			double input = getInputAverage();
			double update = getUpdateAverage();
			double render = getRenderAverage();
			double budgets = getBudgetAverage();
			
			String frameTime = df.format(frames);
			String framePerBudget = NumberFormat.getPercentInstance().format(frames / budgets);

			String inputTime = df.format(input);
			String inputPerFrame = NumberFormat.getPercentInstance().format(input / frames);
			String inputPerBudget = NumberFormat.getPercentInstance().format(input / budgets);
			
			String updateTime = df.format(update);
			String updatePerFrame = NumberFormat.getPercentInstance().format(update / frames);
			String updatePerBudget = NumberFormat.getPercentInstance().format(update / budgets);
			
			String renderTime = df.format(render);
			String renderPerFrame = NumberFormat.getPercentInstance().format(render / frames);
			String renderPerBudget = NumberFormat.getPercentInstance().format(render / budgets);
			
			AvoLog.log().debug("""

					Frame Time: \t{}
					Input Time: \t{}
					Update Time:\t{}
					Render Time:\t{}

					Input/Frame:\t{}
					Update/Frame:\t{}
					Render/Frame:\t{}
					Frame/Budget:\t{}
					Input/Budget:\t{}
					Update/Budget:\t{}
					Render/Budget:\t{}
					""",
					frameTime, inputTime, updateTime, renderTime,
					inputPerFrame, updatePerFrame, renderPerFrame, framePerBudget, inputPerBudget, updatePerBudget, renderPerBudget);
		}
	},
	
	/**
	 * A No-OP profiler that performs no actions while profiling.
	 */
	NO_OP;
}

sealed interface Profilable permits FrameProfiler {
	
	public default void startFrame() {
		// Default No-OP
	}
	
	public default void endFrame() {
		// Default No-OP
	}
	
	public default void endBudget() {
		// Default No-OP
	}
	
	public default void inputStart() {
		// Default No-OP
	}
	
	public default void inputEnd() {
		// Default No-OP
	}
	
	public default void updateStart() {
		// Default No-OP
	}
	
	public default void updateEnd() {
		// Default No-OP
	}
	
	public default void renderStart() {
		// Default No-OP
	}
	
	public default void renderEnd() {
		// Default No-OP
	}
	
	public default void printAverages() {
		// Default No-OP
	}
	
}
