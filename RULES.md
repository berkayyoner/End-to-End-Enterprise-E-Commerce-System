# Project's Main Goal
This will be a enterprise e-commerce project which will include all of the main technologies, designs and expectations on below. Make the project like you are a 20 years experienced Software Architect and make sure UI design is completely enterpise level as all over the architecture.

### This projects dummy name will be "Berkay"

## Technologies
* **Front-end:** React.Js
* **Back-end:** Java Spring Boot (Java 21)
* **Back-end Dependancy and JVM:** Maven
* **Caching:** Redis
* **Containeriastion:** Docker
* **Main product search:** Elasticsearch
* **Environments for all projects:** development, production, local
* **ORM:** Hybernate with Spring JPA
* **Database:** Oracle
* **Server Management:** Kubernates
* **Deployment:** Jenkins
* **Front-end Test Tools:** Selenium
* **Back-end Test Tools:** Unit Test, Postman
* **Authentication:** OAuth 2.0

## Database informations
* **Database user name:** berkay
* **Password:** 1234
* **Port:** 1521
* **Service name:** XEPDB1

## Public Application Permission Groups
* Seller
* Customer
* User (Not signed in)

## Personnel Application Permission Groups
* Inner permission group system.

## Project Names
* **Public front-end:** berkay-public
* **Personnel front-end:** berkay-personnel
* **API:** api
* **Generally:** Put all of the different applications in differend folders and name the folders according to the applications created inside. Some of the project bases already created so use them if needed.

## Expectations
* Always stick to SOLID principles.
* This will be a microservice enterpise project. Each application needs to be kept under different folders. Avoid using general foldering names like "frontend" or "backend".
* No critical or unwanted data will be sent to front-end.
* Create environments for all applications but for development; use localhost values.
* Never delete a value, only soft delete. Also datas should keep dates.
* There will be a multi-language support. Default language will be Turkish. You can use codes in "C:\Users\berka\Desktop\End-to-End-Enterprise-E-Commerce-System\example-bases" folder for implementing and creating a base. Then you can keep developing on top of them if you need to. So it must be easy to add a new language for front-end. Also sellers will decide Turkish and English names of the products so these informations will be kept inside database. So also on the service/database side, make an enterprise level multi-language support which easily lets adding new languages in the future.
* Website can be visited by non-users.
* Admin/moderator app will be a different front-end project and for CORS; only localhost and specified IPs can connect.
* Only signed in users can make purchases. Users can sign up.
* When users sign up, they need to apply from profile section for being a "Seller".
* Users might be able to find, search and see the details of products which will work with elasticsearch. Products must be listed limited. Example: When user searchs, only 20 of the most matching items must be listed and when user scrolls down to approximately the 16th item, a new 20 should be added and it keeps extending with this system. Seached products must be listed in 4 lines.
* Search page needs to have a button which opens as dropdown for "Suggested Ranking", "The Most Expensive", "The Cheapest", "Newest", "The Most Selling", "The Most Favorited", "The Most Rated" filtering options. Also there should be a detailed filtering system vertically on left of the products. Detailed search will include "Categories" which includes all Main Categories, Sub Types and Inner Types with checkbox.
* All products must have price, stock, general description (if user clicks "Show More" it directs to below where there is a detailed description section as rest of the description), avarage rating value, total rating amounts, Q&A section, photo slider (maximum 10 photos), product campaigns section if there are matching ones, buy now (directs to payment), add to basket, follow sellers profile and products section, this sellers all Q&As, go to market (directs to sellers all products list from his profile), estimated delivery days depending on customer and sellers locations, key features boxes, similar products horizontal slider list with product boxes, recomended products horizontal slider list with product boxes, "Customers who bought this product also bought these" products horizontal slider list with product boxes, detailed description, "These might also interest you" section with only product names and not photos, popular brand and stores section with only clickable names, popular pages (products) section with only clickable names.
* There will be a footer section which will include different sub titles and contents under it such as: "who are we?", "Contact", "Security" links, "Campaigns, "Sell on [Project name]", "Live Support", "How may I return" links. Also supported payment networks icons such as "MasterCard", "Visa", "Troy". On a seperate bottom part; there will be social media icons as clickable links and "©2026 All Right Reserved" text, "Cookie Options", "Terms of Use" and "Protection of Personal Data" clickable texts which leads to related pages.
* Top navigation bar needs to have logo picture which leads to homepage when it gets clicked, a search bar for products search which will work with elasticsearch, then "My Account", "My Favorites", "My Basket", light/dark mode and language changing buttons.
* "My Account" button is a dropdown which opens when mouse hovers it. Menu will have "All My Orders", "My Reviews", "My Discount Coupons", "Seller Messages, "My User Information", "Log Out" buttons.
* Below the first line, there will be "Categories" button with 3 lines as icon on left of it which gives the idea of "This is a list" to the users. Main category titles will be listed to right side of the "Categories" button as horizontal. When they are clicked, that categories should automatically searched and filtered in search page.
* When user puts mouse on the "Categories" button, it hovers directly without clicking. "Categories" menu shows main product types vertically. Automaticly hovers the sub types on right side of the main types lists inner types under the sub type titles. Only shows 5 inner types for for each title, 6th button is always "Show More" and leads to items page and filters for that items. So right order of category levels are: Main Categories > Sub Types > Inner Types.
* Admins and moderators defines the main categories, sub categories and inner category types in their panel. If a moderator/admin makes changes in categories, application sends request to admin "Change Requests" list and a admin needs to accept or deny it for it to be effected.
* Every customer needs to verify their IDs to make purchase and for applying to becoming a seller. If they don't verify their IDs and click to "Buy Now", "Add to Basket" or "Sell on [Project name]" buttons project should direct the customer to ID verification page. Make a dummy ID verification page for now which doesn't needs a real ID, accepts any ID number and 2 photos as 2 sides of the ID with clients face. Sends the ID applications to moderator/admin "ID Applications" page. An admin/moderator needs to accept or deny it for being accepted.
* Payment page needs card informations but make it able to accept random card informations as dummy for now. There should be an option for "Save card information for future purchases" which saves the card information if payment is successful. So when a customer wants to make a payment, system should first control that is there a saved card information and if yes, it should fill the boxes automatically.
* Admin panel should directly open up with a login page. After the login, there will be different moderation/administration pages on left of the screen which can be opened and closed. There might be navigaton bar on top of the screen which only has language dropdown menu and logout buttons. Also admin panel will have multi-language support too. Every page in personal menu needs unique code names and descriptions such as "P2 - Product Requests".
* There is a "Permissions" page in admin/moderator application which lets the personnels with P0 permission can create, edit and delete permission groups. Permission groups has a name and permission codes which lets the user to be able to access these pages. Also codes gets added with first latter of "Delete", "Add" and "Edit". If a personnel has only view permission; permission must be like P5, if also has editing permission; P5E, if has editing and adding; P5AE. Personnel Permission Example: "Manager: P0AED, P1AED, P2ED, P3, P4A". P0 user can also edit personnel permission groups to set the right permission for them. You might edit this idea if there is a more optimal way.
* There must be "Personnel" (for creating personnel accounts and editing their permission groups), "Users" (for public application accounts. Also permitted personnel should be able to edit and soft delete accounts.), "ID Applications", "Seller Applications", "Main Categories", "Sub Categories", "Inner Categories", "Products" (should be able to edit and soft delete user products.), "User Logs", "Personnel Logs", Campaigns pages which personnels can use for management. There will be new pages added depending on the features added.
* There must be a banning option in "Users" page of personnel application. Banning should save users all of the informations to banneds table which should include id numbers, phone numbers, ip adresses (if possible), mail adresses.
* For applying to becoming a seller, customer should provide company infos but keep it a dummy application which accepts
* If the account is Seller, there should be a "My Store" button which hovers and includes earned money info, "My Products" page button, "Add New Product" buttons. 
* In "Add New Product" page, sellers should be able to upload maximum 10 photos and fill all the informations which was given before.
* In "My Products" page, sellers should be able to list their products and edit all of the details.
* Every customer and seller must have a profile which customers can follow sellers so their products must be suggested more for them.
* Log every user and personnel activities in both applications and keep the logs in database.
* Create unit and selenium test files too.

## UI Design
* There will be 2 color modes which are light and dark modes.
* Make sure public application is responsive and can be used fully functional in mobile phone/tablets.
* Make sure some of the icons, hover, button, text colors are using red color as a theme and it should be changed easily in codes.

# Important
* Write the Phase and analysis results into ANALYSIS.md file.
* Write the done phases and completed/failed steps into DONE.md and follow this file.