using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using Minio;
using Minio.DataModel;
using Minio.Exceptions;

using System.Text;
using System.Threading.Tasks;


namespace MinioApi.Controllers
{
    public class ValuesController : ApiController
    {

       
        // GET api/values
        //public List<string> Get()
          public String Get()
        {
            var endpoint = "127.0.0.1:9000";
            var accessKey = "VDX5QGGFWH9HFGCQXY42";
            var secretKey = "FSDxipLeNUX7E8Y6ttsXVhaTyGe6mnlCHsP1xWKW";

            // List<string> rez = new List<string>();
            int x = 0;
       //    try
       //     {
                var minio = new MinioClient(endpoint, accessKey, secretKey);
                 var p=GetAsync(minio);
                 x = GetAsync(minio).Result;
            // rez=Run(minio).Result;
            // x = Run(minio).Result;
        

                
      //      }
      //      catch (Exception ex)
      //      {
      //          Console.Out.WriteLine(ex.Message);
      //      }

            //return rez;
            return x.ToString();
        }
       // private async static Task<List<String>> Run(MinioClient minio)
         private async static Task<int> GetAsync(MinioClient minio)
        {
            //List<String> rez = new List<String>();

            //try
            //{
            //       var getListBucketsTask =await minio.ListBucketsAsync();
            //        //   List<Bucket> b = getListBucketsTask.Result.Buckets;

            //    foreach(Bucket bucket in getListBucketsTask.Buckets)
            //    {
            //        rez.Add(bucket.Name.ToString());
            //    }

            //    }
            //    catch (MinioException e)
            //    {
            //        Console.WriteLine("File Upload Error: {0}", e.Message);
            //    }

            //    return rez;

            var bucketName = "mymusic";
            var location = "us-east-1";
            var objectName = "6golden-oldies.zip";
            //var filePath = "C:\\Users\\username\\Downloads\\golden_oldies.mp3";
            var filePath = "C: \\Users\\Marko\\Downloads\\apache-tomcat-8.5.23-windows-x64.zip";
            var contentType = "application/zip";

            int ret = 0;

         
                // Make a bucket on the server, if not already present.
               // minio.SetTraceOff();

                bool found = await minio.BucketExistsAsync(bucketName);
                if (!found)
                {
                     await minio.MakeBucketAsync(bucketName, location);
                }
                // Upload a file to bucket.
               await  minio.PutObjectAsync(bucketName, objectName, filePath, contentType);
             //   Console.Out.WriteLine("Successfully uploaded " + objectName + ret.ToString());

                ret = 1;
         
            return ret;
        }

        // GET api/values/5
        public string Get(int id)
        {

            return "value";
        }

        // POST api/values
        public string Post(String value)
        {
            return value;
        }

        // PUT api/values/5
        public void Put(int id, [FromBody]string value)
        {
        }

        // DELETE api/values/5
        public void Delete(int id)
        {
        }
    }
}
