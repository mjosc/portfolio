/*
 * stackoverflow.com/questions/950087/how-do-i-include-a-javascript-file-in-another-javascript-file
 * https://medium.com/@svinkle/getting-started-with-webpack-and-es6-modules-c465d053d988
 */

class FamilyGraph {
  constructor() {
    this.personMap = new Map();
    this.familyMap = new Map();
  }

  addPerson(p) {
    let id = p.id;
    if (this.personMap.has(id)) {
      return false;
    }
    this.personMap.set(id, p);
    return true;
  }

  addFamily(f) {
    let id = f.id;
    if (this.familyMap.has(id)) {
      return false;
    }
    this.familyMap.set(id, f);
    return true;
  }
}

class Person {
  constructor(id, name) {
    this.id = id;
    this.name = name;
    this.childFamily;
    this.parentFamilies;
  }
}

class Family {
  constructor(id) {
    this.id = id;
    this.parents;
    this.children;
  }
}

let jsonArr = "[{\"id\":1,\"name\":\"me\",\"childFamily\":1,\"parentFamilies\":[]},{\"id\":2,\"name\":\"father\",\"childFamily\":2,\"parentFamilies\":[1]},{\"id\":3,\"name\":\"mother\",\"childFamily\":3,\"parentFamilies\":[1]},{\"id\":4,\"name\":\"grandFatherFather\",\"parentFamilies\":[2]},{\"id\":5,\"name\":\"grandMotherFather\",\"parentFamilies\":[2]},{\"id\":6,\"name\":\"grandFatherMother\",\"parentFamilies\":[3]},{\"id\":7,\"name\":\"grandMotherMother\",\"parentFamilies\":[3]}]"

let data = JSON.parse(jsonArr);

let fgraph = new FamilyGraph();

data.forEach(obj => {
  
  fgraph.addPerson(new Person(obj.id, obj.name));

// TODO: May want to change java-side to make childFamily appear with undefined or something similar
// if it doesn't exist. This would make it more consistent with an empty array for parentFamilies
// if the person does not have any families in which they are a parent.
// 
  if (obj.hasOwnProperty('childFamily')) {
    fgraph.addFamily(new Family(obj.childFamily));
  // TODO: Add edges between family and child.
  }

  let parentFamilies = obj.parentFamilies; // array of ids
  parentFamilies.forEach(id => {
    fgraph.addFamily(new Family(id));
    // TODO: Add edges between family and parent
  });

});

console.log(fgraph.personMap.size);
console.log(fgraph.familyMap.size);

fgraph.familyMap.forEach(element => {
  console.log(element);
});
