@echo off
rem This script rebuilds the Soar parser using JavaCC. Since the generated
rem files are checked in, there should be no need to run this script unless
rem you are making modifications to soarparser.jj.  If you do run it, you'll
rem have to refresh this directory in Eclipse before you'll see the changes.
call ..\..\..\..\..\..\..\..\tools\javacc-4.0\bin\javacc.bat soarparser.jj
