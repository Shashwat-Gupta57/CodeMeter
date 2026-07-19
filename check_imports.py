import sys
import pefile

def check_imports(exe_path):
    pe = pefile.PE(exe_path)
    print(f"Imports for {exe_path}:")
    for entry in pe.DIRECTORY_ENTRY_IMPORT:
        dll_name = entry.dll.decode('utf-8').lower()
        print(f"- {dll_name}")
        if 'vcruntime' in dll_name or 'msvcp' in dll_name:
            print("  *** FOUND MSVC RUNTIME DEPENDENCY ***")

if __name__ == "__main__":
    check_imports(sys.argv[1])
