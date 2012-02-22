
#include <iostream>
#include <string>
#include <stdlib.h>
#include <iomanip>
#include <sstream>
#include <cstdio>
#include <fstream>

#include "sml_Client.h"
#include "sml_Names.h"

using namespace std;
using namespace sml;

// Conversion of value to string
template<class T> std::string& toString( const T& x, std::string& dest )
{
	static std::ostringstream o;
	
	// get value into stream
	o << std::setprecision( 16 ) << x;
	
	dest.assign( o.str() );
	o.str("");
	return dest;
}

// Conversion from string to value
template <class T> bool fromString( T& val, const std::string& str )
{
	std::istringstream i( str );
	i >> val;
	return !i.fail();
}

int main( int argc, char* argv[] )
{
	if ( argc != 2 )
	{
		return 1;
	}

	int numDecisions;
	{
		string in( argv[1] );
		fromString( numDecisions, in );
	}

	// do stuff
	{
		// create kernel
		Kernel* pKernel = Kernel::CreateKernelInNewThread();
		if ( pKernel->HadError() )
		{
			cout << pKernel->GetLastErrorDescription() << endl;
			return 0;
		}
		
		// create agent
		Agent* pAgent = pKernel->CreateAgent( "headless" );
		{
			if ( pKernel->HadError() )
			{
				cout << pKernel->GetLastErrorDescription() << endl;
				return 0;
			}
		}
		
		// source rules, set stuff
		{
			pAgent->LoadProductions( "agent.soar" );
			
			// watch 0
			pAgent->ExecuteCommandLine( "watch 0" );

			// wma
			pAgent->ExecuteCommandLine( "wma --set activation on" );
		}

		// run
		{
			ofstream* f;
			string temp, fname;

			for ( int i=1; i<=numDecisions; i++ )
			{
				toString( i, temp );
				fname.assign( "wma_" );
				fname.append( temp );
				fname.append( ".txt" );

				f = new ofstream( fname.c_str() );

				pAgent->ExecuteCommandLine( "d" );

				(*f) << pAgent->ExecuteCommandLine( "print --internal --depth 10 s1" );

				f->close();
				delete f;
			}
		}
			
		// clean-up
		{
			pKernel->Shutdown();
			delete pKernel;
		}
	}
	
	return 0;
}
