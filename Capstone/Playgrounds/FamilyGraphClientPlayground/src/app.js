import { FamilyGraph, Person, Family } from './FamilyGraph.js'

let fgraph = new FamilyGraph();

/*
 * Mock direct-line data spanning 3 generations where all persons are connected to both parents.
 * The data received by an api call to get the direct line ancestry for id:1.
 * 
 * 7 persons
 * 3 families
 */
let jsonArr = "[{\"id\":1,\"name\":\"me\",\"childFamily\":1,\"parentFamilies\":[]},{\"id\":2,\"name\":\"father\",\"childFamily\":2,\"parentFamilies\":[1]},{\"id\":3,\"name\":\"mother\",\"childFamily\":3,\"parentFamilies\":[1]},{\"id\":4,\"name\":\"grandFatherFather\",\"parentFamilies\":[2]},{\"id\":5,\"name\":\"grandMotherFather\",\"parentFamilies\":[2]},{\"id\":6,\"name\":\"grandFatherMother\",\"parentFamilies\":[3]},{\"id\":7,\"name\":\"grandMotherMother\",\"parentFamilies\":[3]}]"

let data = JSON.parse(jsonArr);

data.forEach(obj => {
  fgraph.addPerson(new Person(obj.id, obj.name));
  if (obj.hasOwnProperty('childFamily')) {
    // This person is listed as a child in at least one family.
    /* TODO
     * There may be a lot of overhead, creating a new Family instance before comfirming whether that
     * family already exists. This may require some refactoring of the FamilyGraph methods to simply
     * first check whether a Family exists before creating one.
     */ 
    fgraph.addFamily(new Family(obj.childFamily));
    /* TODO
     * Add bidirectional edges (child <-> family).
     */ 
  }
  obj.parentFamilies.forEach(id => {
    // Each person will have 0 or more familes in which they are a parent.
    fgraph.addFamily(new Family(id))
    /* TODO
     * Add bidirectional edges (parent <-> family).
     */ 
  });

});

console.log(fgraph.personMap.size);
console.log(fgraph.familyMap.size);

fgraph.personMap.forEach(val => {
  console.log(val);
});

fgraph.familyMap.forEach(val => {
  console.log(val);
});
