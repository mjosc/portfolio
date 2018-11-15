"use strict";

var _FamilyGraph = require("./FamilyGraph.js");

var parseChildFamilies = function parseChildFamilies(person, childFamilies) {
  childFamilies.forEach(function (id) {
    var family = void 0;
    if (!fgraph.hasFamily(id)) {
      family = new _FamilyGraph.Family(id);
      fgraph.addFamily(family);
    } else {
      family = fgraph.getFamily(id);
    }
    fgraph.addChildFamilyEdge(person, family);
  });
}; // https://www.sitepoint.com/javascript-private-class-fields/

var parseParentFamilies = function parseParentFamilies(person, parentFamilies) {
  parentFamilies.forEach(function (id) {
    var family = void 0;
    if (!fgraph.hasFamily(id)) {
      family = new _FamilyGraph.Family(id);
      fgraph.addFamily(family);
    } else {
      family = fgraph.getFamily(id);
    }
    fgraph.addParentFamilyEdge(person, family);
  });
};

var fgraph = new _FamilyGraph.FamilyGraph();

/*
 * Mock direct-line data spanning 3 generations where all persons are connected to both parents.
 * The data received by an api call to get the direct line ancestry for id:1.
 * 
 * 7 persons
 * 3 families
 */
var jsonArr = "[{\"id\":1,\"name\":\"me\",\"childFamilies\":[1],\"parentFamilies\":[]},{\"id\":2,\"name\":\"father\",\"childFamilies\":[2],\"parentFamilies\":[1]},{\"id\":3,\"name\":\"mother\",\"childFamilies\":[3],\"parentFamilies\":[1]},{\"id\":4,\"name\":\"grandFatherFather\",\"childFamilies\":[],\"parentFamilies\":[2]},{\"id\":5,\"name\":\"grandMotherFather\",\"childFamilies\":[],\"parentFamilies\":[2]},{\"id\":6,\"name\":\"grandFatherMother\",\"childFamilies\":[],\"parentFamilies\":[3]},{\"id\":7,\"name\":\"grandMotherMother\",\"childFamilies\":[],\"parentFamilies\":[3]}]";

var data = JSON.parse(jsonArr);

data.forEach(function (obj) {

  var p = new _FamilyGraph.Person(obj.id, obj.name);
  fgraph.addPerson(p);

  parseChildFamilies(p, obj.childFamilies);
  parseParentFamilies(p, obj.parentFamilies);
});

var dl = fgraph.directLineTraversal(fgraph.getPerson(1));

var children = fgraph.childrenInFamily(fgraph.getFamily(2));
children.forEach(function (child) {
  return console.log(child.name);
});