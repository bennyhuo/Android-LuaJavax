print = function(...)
    io.write(table.concat({ os.date("[%Y-%m-%d %H:%M:%S]"), ... }, "\t"))
    io.flush()
end

function redirect_output(path)
    io.output(path)
end