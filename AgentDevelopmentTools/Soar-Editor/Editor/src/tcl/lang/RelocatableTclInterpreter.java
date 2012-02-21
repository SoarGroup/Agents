package tcl.lang;

import java.io.File;

import tcl.lang.Interp;

public class RelocatableTclInterpreter extends Interp {
	public interface ProgressHandler {
		void onProgress(RelocatableTclInterpreter interp);
	}

	public static class InterruptedException extends RuntimeException {
		private static final long serialVersionUID = 8406042835724419511L;

		public InterruptedException() {
			super("Tcl processing interrupted");
		}

	}
	
	public RelocatableTclInterpreter() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tcl.lang.Interp#getWorkingDir()
	 */
	@Override
	public File getWorkingDir() {
		return super.getWorkingDir();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tcl.lang.Interp#setWorkingDir(java.lang.String)
	 */
	@Override
	public void setWorkingDir(String arg0) throws TclException {
		super.setWorkingDir(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tcl.lang.Interp#getCommand(java.lang.String)
	 */
	@Override
	public Command getCommand(String arg0) {
		// In Jacl 1.3 this is the easiest way I found to handle progress while
		// Tcl is executing. This method is called every time a Tcl command is
		// executed so it's a good place to hook in and handle cancellation of
		// infinite loops. In Jacl 1.4 (which as of 4/20/2007 hasn't been
		// called stable yet), the ready() method of the interpreter may be
		// a better choice to override.
		return super.getCommand(arg0);
	}

	public int getErrorLine()
    {
        return errorLine;
    }
}
