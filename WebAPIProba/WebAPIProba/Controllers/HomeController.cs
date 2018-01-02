using Minio;
using Minio.DataModel;
using Minio.Exceptions;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web;
using System.Web.Mvc;
using System.Web.Script.Serialization;
using System.Text;
using System.Reactive.Linq;
using System.Drawing;
using System.IO;

namespace WebAPIProba.Controllers
{
    public class HomeController : Controller
    {

     

        public async Task<int> DeletePhoto(String username, String name)
        {
            var endpoint = "127.0.0.1:9000";
            var accessKey = "VDX5QGGFWH9HFGCQXY42";
            var secretKey = "FSDxipLeNUX7E8Y6ttsXVhaTyGe6mnlCHsP1xWKW";
            var minio = new MinioClient(endpoint, accessKey, secretKey);

            var mybucket = username.ToLower();
            var myobject = name;

            try
            {
                await minio.RemoveObjectAsync(mybucket, myobject);
                Console.Out.WriteLine("successfully removed all incomplete upload session of my-bucketname/my-objectname");
            }
            catch (MinioException e)
            {
                Console.Out.WriteLine("Error occurred: " + e);
                return 0;
            }

            return 1;
        }

        //  [HttpPost]
        public async Task<String> Load(String username,String name)
        {
            String s = "";

            var endpoint = "127.0.0.1:9000";
            var accessKey = "VDX5QGGFWH9HFGCQXY42";
            var secretKey = "FSDxipLeNUX7E8Y6ttsXVhaTyGe6mnlCHsP1xWKW";
            var minio = new MinioClient(endpoint, accessKey, secretKey);

            var bucketName = username.ToLower();
            var location = "us-east-1";
            var objectName = name;
            var contentType = "application/octet-stream";
            Stream inputStream=null;
            try
            {
                // Check whether the object exists using statObjectAsync().
                // If the object is not found, statObjectAsync() throws an exception,
                // else it means that the object exists.
                // Execution is successful.
                await minio.StatObjectAsync(bucketName,objectName);

                // Gets the object's data and stores it in photo.jpg
                //  await minio.GetObjectAsync(bucketName, objectName, "photo.jpg");
                byte[] imageBytes= new byte[1];
                await minio.GetObjectAsync(bucketName, objectName,
                (stream) =>
                {
                    imageBytes = ReadFully(stream);
                    //stream.CopyTo(inputStream);
                });

                //imageBytes = ReadFully(inputStream);
                s = Convert.ToBase64String(imageBytes);
                //MemoryStream imagestream = new MemoryStream(imageBytes);
                //bitmap = new Bitmap(imagestream);


            }
            catch (MinioException e)
            {
                Console.Out.WriteLine("Error occurred: " + e);
            }

            return s;
        }
    

        [HttpPost]
        public async Task<int> Upload(String encode, String name, String username)
        {
            var endpoint = "127.0.0.1:9000";
            var accessKey = "VDX5QGGFWH9HFGCQXY42";
            var secretKey = "FSDxipLeNUX7E8Y6ttsXVhaTyGe6mnlCHsP1xWKW";
            var minio = new MinioClient(endpoint, accessKey, secretKey);
     
            var bucketName = username.ToLower();
            var location = "us-east-1";
            var objectName = name;
            var contentType = "application/octet-stream";
            int mod4 = encode.Length % 4;

            byte[] bs = Convert.FromBase64String(encode);
            System.IO.MemoryStream filestream = new System.IO.MemoryStream(bs);

            bool found = await minio.BucketExistsAsync(bucketName);
            if (!found)
            {
                await minio.MakeBucketAsync(bucketName, location);
            }
            await minio.PutObjectAsync(bucketName,objectName,filestream,filestream.Length,contentType);

            return 1;
        }


        [HttpPost]
        public int Login(String username)
        {
            Session["username"] = username;
            return 1;
        }

        public async Task<int> DeleteNote(String username, String name)
        {
            var endpoint = "127.0.0.1:9000";
            var accessKey = "VDX5QGGFWH9HFGCQXY42";
            var secretKey = "FSDxipLeNUX7E8Y6ttsXVhaTyGe6mnlCHsP1xWKW";
            var minio = new MinioClient(endpoint, accessKey, secretKey);

            var mybucket = username.ToLower();
            var myobject = name;
            var myobject1 = name + ".jpg";
            var myobject2 = name + ".txt";

            try
            {
                await minio.RemoveObjectAsync(mybucket, myobject1);
                await minio.RemoveObjectAsync(mybucket, myobject2);
                Console.Out.WriteLine("successfully removed all incomplete upload session of my-bucketname/my-objectname");
            }
            catch (MinioException e)
            {
                Console.Out.WriteLine("Error occurred: " + e);
                return 0;
            }

            return 1;
        }

        public async Task<String> ListAllBuckets()
        {
            var endpoint = "127.0.0.1:9000";
            var accessKey = "VDX5QGGFWH9HFGCQXY42";
            var secretKey = "FSDxipLeNUX7E8Y6ttsXVhaTyGe6mnlCHsP1xWKW";
            var minio = new MinioClient(endpoint, accessKey, secretKey);

            List<String> rez = new List<String>();
            try
            {
                var getListBucketsTask = await minio.ListBucketsAsync();
                //   List<Bucket> b = getListBucketsTask.Result.Buckets;

                foreach (Bucket bucket in getListBucketsTask.Buckets)
                {
                    rez.Add(bucket.Name.ToString());
                    //  await minio.MakeBucketAsync(bucket.Name.ToString()+"/testBucket");
                }
            }
            catch (MinioException e)
            {
                Console.WriteLine("File Upload Error: {0}", e.Message);
            }
            String json = new JavaScriptSerializer().Serialize(rez);
            return json;
        }


        public ActionResult About()
        {
            ViewBag.Message = "Your application description page.";

            return View();
        }

        public ActionResult Contact()
        {
            ViewBag.Message = "Your contact page.";

            return View();
        }


        public static byte[] ReadFully(Stream input)
        {
            byte[] buffer = new byte[16 * 1024];
            using (MemoryStream ms = new MemoryStream())
            {
                int read;
                while ((read = input.Read(buffer, 0, buffer.Length)) > 0)
                {
                    ms.Write(buffer, 0, read);
                }
                return ms.ToArray();
            }
        }

    }
}