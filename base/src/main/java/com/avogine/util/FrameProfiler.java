package com.avogine.util;

import java.text.DecimalFormat;
import java.util.*;

import com.avogine.logging.AvoLog;

/**
 *
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
			String framePerBudget = DecimalFormat.getPercentInstance().format(frames / budgets);

			String inputTime = df.format(input);
			String inputPerFrame = DecimalFormat.getPercentInstance().format(input / frames);
			String inputPerBudget = DecimalFormat.getPercentInstance().format(input / budgets);
			
			String updateTime = df.format(update);
			String updatePerFrame = DecimalFormat.getPercentInstance().format(update / frames);
			String updatePerBudget = DecimalFormat.getPercentInstance().format(update / budgets);
			
			String renderTime = df.format(render);
			String renderPerFrame = DecimalFormat.getPercentInstance().format(render / frames);
			String renderPerBudget = DecimalFormat.getPercentInstance().format(render / budgets);
			
			AvoLog.log().debug("""

					Frame Time: 	{}
					Input Time: 	{}
					Update Time:	{}
					Render Time:	{}

					Input/Frame:	{}
					Update/Frame:	{}
					Render/Frame:	{}
					Frame/Budget:	{}
					Input/Budget:	{}
					Update/Budget:	{}
					Render/Budget:	{}
					""",
					frameTime, inputTime, updateTime, renderTime,
					inputPerFrame, updatePerFrame, renderPerFrame, framePerBudget, inputPerBudget, updatePerBudget, renderPerBudget);
		}
	},
	
	/**
	 * A No-Op profiler that performs no actions while profiling.
	 */
	NO_OP;

	@Override
	public void startFrame() {}

	@Override
	public void endFrame() {}

	@Override
	public void endBudget() {}
	
	@Override
	public void inputStart() {}
	
	@Override
	public void inputEnd() {}

	@Override
	public void updateStart() {}

	@Override
	public void updateEnd() {}

	@Override
	public void renderStart() {}

	@Override
	public void renderEnd() {}

	@Override
	public void printAverages() {}

}

sealed interface Profilable permits FrameProfiler {
	
	public void startFrame();
	
	public void endFrame();
	
	public void endBudget();
	
	public void inputStart();
	
	public void inputEnd();
	
	public void updateStart();
	
	public void updateEnd();
	
	public void renderStart();
	
	public void renderEnd();
	
	public void printAverages();
	
}
