using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Threading.Tasks;

namespace AJEFDService3
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        /// 

        static void Main()
        {

#if DEBUG
            MyService myService = new MyService();
            myService.onDebug();
            System.Threading.Thread.Sleep(System.Threading.Timeout.Infinite);
#else
            ServiceBase[] ServicesToRun;
            ServicesToRun = new ServiceBase[]
            {
                new MyService()
            };
            ServiceBase.Run(ServicesToRun);
#endif
        }






        //static void Main()
        //{
        //    ServiceBase[] ServicesToRun;
        //    ServicesToRun = new ServiceBase[]
        //    {
        //        new MyService()
        //    };
        //    ServiceBase.Run(ServicesToRun);
        //}
    }
}
