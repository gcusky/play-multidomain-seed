define ["jquery", "bootstrap"], ($, bootstrap) ->

	# inner vars and functions
	_world = "World"
	_helloTo = (name) -> "Hello #{name}!"
	_helloWorld = -> _helloTo _world

	# every here will be "packed" into the returned object with these attributes visible from outside.
	# 每个在这里将被“打包”到返回的对象中，这些属性可以从外部看到。
	helloTo: _helloTo
	helloWorld: _helloWorld
	showModal: ($modal) -> $modal.modal 'show'
	hideModal: ($modal) -> $modal.modal 'hide'