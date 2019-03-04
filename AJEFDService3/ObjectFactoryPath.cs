using System;
using System.IO;
using System.Reflection;
using System.Linq;
using IPathLibrary;


namespace AJEFDService3
{
    class ObjectFactoryPath
    {


        public static IPath Create(String[] fileInfo)
        {
            string getPath;
            //String[] info;
            String[] myList;
            myList = fileInfo;
            IPath IObj;
            String dllFileName = myList[1];
            String nameSpace = myList[2];
            String className = myList[3];

            string thisClientDirectory = GetThisAssemblyDirectory();
            string dllPath = Path.Combine(thisClientDirectory, dllFileName);
            if (!File.Exists(dllPath))
            {
                Console.WriteLine("ERROR: File " + dllPath + " does not exist");

            }

            // Load the DLL
            Assembly customDLL = Assembly.LoadFrom(dllPath);

            Type t = customDLL.GetType(className);
            if (t == null)
            {
                Console.WriteLine("ERROR: Can't find class named " + dllFileName + " for now in DLL " + dllPath);

            }


            // Create an instance of the class.
            object o = Activator.CreateInstance(t);

            // Check if o is of type IPath.
            if (!(o is IPath))
            {
                Console.WriteLine("ERROR: Class :: " + className + "   does not implement interface IPath");
            }


            IPath customerObj = (IPath)o;
            getPath = customerObj.getPath();
            ExternalComcs ec = new ExternalComcs(getPath);
            IObj = customerObj;
            return IObj;

        }
        private static string GetThisAssemblyDirectory()
        {
            string codeBase = Assembly.GetExecutingAssembly().CodeBase;
            UriBuilder uri = new UriBuilder(codeBase);
            string path = Uri.UnescapeDataString(uri.Path);
            return Path.GetDirectoryName(path);
        }
    }
}
