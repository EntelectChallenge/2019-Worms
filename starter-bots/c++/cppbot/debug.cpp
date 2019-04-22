
#ifdef _WIN32
#include <Windows.h>
#include <stdio.h>
#include <sstream>
#endif

void DebugAttach()
{
#ifdef _WIN32
#ifdef _DEBUG

  std::ostringstream os;
  os << L"Process ID: " << GetCurrentProcessId();
  MessageBox(nullptr, "attach now", os.str().c_str(), MB_OK);
#endif
#endif
}