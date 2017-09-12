webpackJsonp([0],{520:function(e,t,n){"use strict";function a(e){return function(){var t=e.apply(this,arguments);return new Promise(function(e,n){function a(r,o){try{var l=t[r](o),i=l.value}catch(e){return void n(e)}if(!l.done)return Promise.resolve(i).then(function(e){a("next",e)},function(e){a("throw",e)});e(i)}return a("next")})}}function r(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function o(e,t){if(!e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return!t||"object"!==typeof t&&"function"!==typeof t?e:t}function l(e,t){if("function"!==typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function, not "+typeof t);e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,enumerable:!1,writable:!0,configurable:!0}}),t&&(Object.setPrototypeOf?Object.setPrototypeOf(e,t):e.__proto__=t)}Object.defineProperty(t,"__esModule",{value:!0});var i=n(136),s=n.n(i),c=n(0),u=n.n(c),p=n(135),f=n(227),d=n.n(f),m=n(522),h=n(524),y=function(){function e(e,t){for(var n=0;n<t.length;n++){var a=t[n];a.enumerable=a.enumerable||!1,a.configurable=!0,"value"in a&&(a.writable=!0),Object.defineProperty(e,a.key,a)}}return function(t,n,a){return n&&e(t.prototype,n),a&&e(t,a),t}}(),b=function(e){function t(){var e,n,l,i,c=this;r(this,t);for(var u=arguments.length,p=Array(u),f=0;f<u;f++)p[f]=arguments[f];return n=l=o(this,(e=t.__proto__||Object.getPrototypeOf(t)).call.apply(e,[this].concat(p))),l.state={showModal:!1,isModify:!1,employee:{},employees:[],links:{},pageIndex:0,pageSize:10,totalPages:0},l.loadData=function(){var e=l.state,n=e.pageIndex,a=e.pageSize;return d.a.get(t.API+"?page="+n+"&size="+a).then(l.handleLoadSuccess)},l.handleLoadSuccess=function(e){return l.setState({employees:e.data._embedded.employees,links:e.data._links,pageIndex:e.data.page.number,pageSize:e.data.page.size,totalPages:e.data.page.totalPages}),e},l.handleCloseModal=function(){l.setState({isModify:!1,employee:{},showModal:!1})},l.handleOpenModal=a(s.a.mark(function e(){return s.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:l.setState({isModify:!1,showModal:!0});case 1:case"end":return e.stop()}},e,c)})),l.handleSubmit=function(){var e=a(s.a.mark(function e(n){return s.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:if(!n._links.self){e.next=5;break}return e.next=3,d.a.put(n._links.self.href,n);case 3:e.next=7;break;case 5:return e.next=7,d.a.post(""+t.API,n);case 7:l.handleCloseModal(),l.loadData();case 9:case"end":return e.stop()}},e,c)}));return function(t){return e.apply(this,arguments)}}(),l.onNavgate=function(e,t){t.preventDefault(),l.setState({pageIndex:e},l.loadData)},l.onUpdate=function(e){return function(t){t.preventDefault(),l.setState({employee:e,isModify:!0,showModal:!0})}},l.onDelete=function(e){return function(t){t.preventDefault(),d.a.delete(e._links.self.href).then(l.loadData).then(function(e){0===e.data._embedded.employees.length&&l.state.pageIndex>0&&l.setState({pageIndex:l.state.pageIndex-1},l.loadData)})}},i=n,o(l,i)}return l(t,e),y(t,[{key:"componentDidMount",value:function(){function e(){return t.apply(this,arguments)}var t=a(s.a.mark(function e(){return s.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:this.loadData();case 1:case"end":return e.stop()}},e,this)}));return e}()},{key:"render",value:function(){return u.a.createElement("div",null,u.a.createElement(p.a,{bsStyle:"primary",onClick:this.handleOpenModal},"Create"),u.a.createElement(h.a,{isModify:this.state.isModify,show:this.state.showModal,data:this.state.employee,onClose:this.handleCloseModal,onSubmit:this.handleSubmit}),u.a.createElement(m.a,{employees:this.state.employees,links:this.state.links,pageIndex:this.state.pageIndex,totalPages:this.state.totalPages,onUpdate:this.onUpdate,onDelete:this.onDelete,onNavgate:this.onNavgate}))}}]),t}(c.Component);b.API="/api/employee",t.default=b},522:function(e,t,n){"use strict";var a=n(0),r=n.n(a),o=n(523),l=function(e){var t=(e.schema,e.employee),n=e.onUpdate,a=e.onDelete;return r.a.createElement("tr",null,r.a.createElement("td",null,t.name),r.a.createElement("td",null,t.age),r.a.createElement("td",null,t.description),r.a.createElement("td",null,r.a.createElement("a",{role:"button",onClick:n},"Update"),r.a.createElement("a",{role:"button",style:{marginLeft:4},onClick:a},"Delete")))},i=function(e){var t=e.employees,n=e.onUpdate,a=e.onDelete;return r.a.createElement("table",{className:"table"},r.a.createElement("thead",null,r.a.createElement("tr",null,r.a.createElement("th",null,"Name"),r.a.createElement("th",null,"Age"),r.a.createElement("th",null,"Description"),r.a.createElement("th",null,"Action"))),r.a.createElement("tbody",null,t&&t.map(function(e){return r.a.createElement(l,{key:e._links.self.href,employee:e,onUpdate:n(e),onDelete:a(e)})})))};t.a=function(e){var t=e.employees,n=e.links,a=e.pageIndex,l=e.totalPages,s=e.onUpdate,c=e.onDelete,u=e.onNavgate;return r.a.createElement("div",null,r.a.createElement(i,{employees:t,onUpdate:s,onDelete:c}),r.a.createElement(o.a,{links:n,pageIndex:a,totalPages:l,onNavgate:u}))}},523:function(e,t,n){"use strict";var a=n(0),r=n.n(a),o=n(135);t.a=function(e){var t=e.links,n=e.pageIndex,a=e.totalPages,l=e.onNavgate;return r.a.createElement(o.k,{first:"first"in t,prev:"prev"in t,next:"next"in t,last:"last"in t,activePage:n+1,items:a,onSelect:function(e,t){return l(e-1,t)}})}},524:function(e,t,n){"use strict";function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function r(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function o(e,t){if(!e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return!t||"object"!==typeof t&&"function"!==typeof t?e:t}function l(e,t){if("function"!==typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function, not "+typeof t);e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,enumerable:!1,writable:!0,configurable:!0}}),t&&(Object.setPrototypeOf?Object.setPrototypeOf(e,t):e.__proto__=t)}var i=n(0),s=n.n(i),c=n(135),u=function(){function e(e,t){for(var n=0;n<t.length;n++){var a=t[n];a.enumerable=a.enumerable||!1,a.configurable=!0,"value"in a&&(a.writable=!0),Object.defineProperty(e,a.key,a)}}return function(t,n,a){return n&&e(t.prototype,n),a&&e(t,a),t}}(),p=function(e){function t(e){r(this,t);var n=o(this,(t.__proto__||Object.getPrototypeOf(t)).call(this,e));return n.handleChange=function(e){var t=e.target,r="number"===t.type,o=t.id,l="checkbox"===t.type?t.checked:t.value;n.setState({data:Object.assign({},n.state.data,a({},o,r?Number(l):l))})},n.triggerSubmit=function(e){e.preventDefault();var t=n.props.onSubmit;t&&t(n.state.data)},n.state={data:e.data||{}},n}return l(t,e),u(t,[{key:"componentWillReceiveProps",value:function(e){"data"in e&&this.setState({data:e.data})}},{key:"render",value:function(){var e=this.props,t=e.show,n=e.isModify,a=e.onClose,r=this.state.data;return s.a.createElement(c.g,{show:t,onHide:a},s.a.createElement(c.g.Header,{closeButton:!0},s.a.createElement(c.g.Title,null,n?"Update":"Create"," employee")),s.a.createElement(c.g.Body,null,s.a.createElement(c.d,{horizontal:!0,onSubmit:this.triggerSubmit},s.a.createElement(c.f,{controlId:"name"},s.a.createElement(c.b,{componentClass:c.c,sm:2},"Name"),s.a.createElement(c.b,{sm:10},s.a.createElement(c.e,{type:"text",onChange:this.handleChange,value:r.name||""}))),s.a.createElement(c.f,{controlId:"age"},s.a.createElement(c.b,{componentClass:c.c,sm:2},"Age"),s.a.createElement(c.b,{sm:10},s.a.createElement(c.e,{type:"number",onChange:this.handleChange,value:r.age||""}))),s.a.createElement(c.f,{controlId:"description"},s.a.createElement(c.b,{componentClass:c.c,sm:2},"Description"),s.a.createElement(c.b,{sm:10},s.a.createElement(c.e,{type:"text",onChange:this.handleChange,value:r.description||""}))),s.a.createElement(c.f,null,s.a.createElement(c.b,{smOffset:2,sm:10},s.a.createElement(c.a,{bsStyle:"primary",type:"submit"},"Save"))))))}}]),t}(i.Component);t.a=p}});
//# sourceMappingURL=0.d7988c2b.chunk.js.map