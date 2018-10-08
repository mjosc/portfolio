// https://www.sitepoint.com/javascript-private-class-fields/

import { FamilyGraph, Person, Family } from './FamilyGraph.js'

let parseChildFamilies = function(person, childFamilies) {
  childFamilies.forEach(id => {
    let family;
    if (!fgraph.hasFamily(id)) {
      family = new Family(id);
      fgraph.addFamily(family);
    } else {
      family = fgraph.getFamily(id);
    }
    fgraph.addChildFamilyEdge(person, family);
  });
}

let parseParentFamilies = function(person, parentFamilies) {
  parentFamilies.forEach(id => {
    let family;
    if (!fgraph.hasFamily(id)) {
      family = new Family(id);
      fgraph.addFamily(family);
    } else {
      family = fgraph.getFamily(id);
    }
    fgraph.addParentFamilyEdge(person, family);
  });
}

let fgraph = new FamilyGraph();

/*
 * Mock direct-line data spanning 3 generations where all persons are connected to both parents.
 * The data received by an api call to get the direct line ancestry for id:1.
 * 
 * 7 persons
 * 3 families
 */
let jsonArr = "[{\"id\":1,\"name\":\"me\",\"childFamilies\":[1],\"parentFamilies\":[]},{\"id\":2,\"name\":\"father\",\"childFamilies\":[2],\"parentFamilies\":[1]},{\"id\":3,\"name\":\"mother\",\"childFamilies\":[3],\"parentFamilies\":[1]},{\"id\":4,\"name\":\"grandFatherFather\",\"childFamilies\":[],\"parentFamilies\":[2]},{\"id\":5,\"name\":\"grandMotherFather\",\"childFamilies\":[],\"parentFamilies\":[2]},{\"id\":6,\"name\":\"grandFatherMother\",\"childFamilies\":[],\"parentFamilies\":[3]},{\"id\":7,\"name\":\"grandMotherMother\",\"childFamilies\":[],\"parentFamilies\":[3]}]"

let data = JSON.parse(jsonArr);

data.forEach(obj => {

  let p = new Person(obj.id, obj.name);
  fgraph.addPerson(p);

  parseChildFamilies(p, obj.childFamilies);
  parseParentFamilies(p, obj.parentFamilies);

});


let dl = fgraph.directLineTraversal(fgraph.getPerson(1));

let children = fgraph.childrenInFamily(fgraph.getFamily(2));
children.forEach(child => console.log(child.name));

