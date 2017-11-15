This project will represent the product categores in a classsic master-detail fashion.

Done:
 1. a sidebar to select categores.
 2. a scrollable category list.
 3. mixed view with static featured brands.
 4. categories page with up to 3 levels.
 
TODO:
 1. first-letter shortcut navigation.
 
Design notes:
 1. Use framework loader APIs to asychronousely load data;
 2. Use Glide to load and render images;
 2. JSON format definition:
	 1) category: 
	   
	   [
		   {
			"name":"costume",  # keyword of a category, used to query a specific sub-category list.
			"label":"服饰"     # displable name of the category.
		   },
		   ...
		]

	   -> see categores.json for more samples.
	   
	   
	 2) sub-categoy:
	 
	 [
	  {
		"title": "Women's Dress",  # displable name of the sub category.
		"display": "grid",         # display format of this item, can either be grid or list.
		"items": [                 # list of items that belongs to the sub category.
		  {
			"name": "Dress",
			"label": "Dress",
			"icon": "dress.png"
		  },
		  ...
		]
	  },
	  ...
	]  

Notes on pakcage structure:

├─assets                 # where local JSON data resides in.
├─java
│  └─com
│      └─imaygou
│          ├─dal         # Data access layer, to retrieve data from either device or remote server.
│          ├─entities    # entities of data object.
│          └─fragments   # collections of fragments.
└─res
    ├─drawable
    ├─drawable-hdpi
    ├─drawable-mdpi
    ├─drawable-xhdpi
    ├─drawable-xxhdpi
    ├─drawable-xxxhdpi
    ├─layout
    ├─layout-w900dp
    ├─mipmap-hdpi
    ├─mipmap-mdpi
    ├─mipmap-xhdpi
    ├─mipmap-xxhdpi
    ├─mipmap-xxxhdpi
    └─values

   