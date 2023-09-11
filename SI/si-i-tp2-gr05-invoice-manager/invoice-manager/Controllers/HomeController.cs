using invoice_manager.Models;
using Microsoft.AspNetCore.Mvc;
using System.Diagnostics;
using System.Net.Http.Headers;
using System.Text;
using System.Xml;


namespace invoice_manager.Controllers
{
    public class HomeController : Controller
    {
        private readonly ILogger<HomeController> _logger;
        private readonly InvoiceModel model;
        private static String oldFilter;

        public HomeController(ILogger<HomeController> logger)
        {
            _logger = logger;
            model = new InvoiceModel();
        }

        public IActionResult Index()
        {
            Console.WriteLine("Default page");
            return Redirect("/Home/Order/True/Id");
            
        }
        public IActionResult Order(String order, String filter)
        {
            Console.WriteLine("Check if double click: " + filter + " <-> " + oldFilter);
            if (filter.Equals(oldFilter))
            {
                oldFilter = "";
                String newOrder = order == "True" ? "False" : "True";
                Console.WriteLine("New order: " + newOrder);
                return Redirect("/Home/Order/"+newOrder+"/"+filter);
            }
            oldFilter = filter;
            Console.WriteLine("Get all invoices");
            List<LightBill> data = model.GetAllInvoices(filter, order);
            Console.WriteLine(data.Count + " invoices found");
            return View(data);
        }
       
        public IActionResult Invoice(String id)
        {
            if (id == null)
            {
                Console.WriteLine("No id in url... redirection to index");
                return RedirectToAction("Order");
            }
            Console.WriteLine("Get invoice " + id + " info");
            Bill data = model.GetInvoice(id);
            Console.WriteLine("Infos found: "+data.Id);
            return View(data);
        }

        public IActionResult Download(String id, String type)
        {
            _logger.LogInformation("Download: " + id + " type: " + type);
            var doc = "";
            var fileName = id + "." + type;

            if(type == "xml")
            {
                 doc = model.GetXml(id);
            }
            else
            {
                doc = model.GetJson(id);

            }


            var stream = new MemoryStream(Encoding.ASCII.GetBytes(doc));
            return new FileStreamResult(stream,"text/plain")
            {
                FileDownloadName = fileName
            };
        }

        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }
    }
}