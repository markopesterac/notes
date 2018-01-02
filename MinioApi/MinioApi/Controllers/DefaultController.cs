using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Minio;
using Minio.DataModel;
using Minio.Exceptions;

namespace MinioApi.Controllers
{
    public class DefaultController : AsyncController
    {
        // GET: Default
        public ActionResult Index()
        {
            return View();
        }

     
    }
}